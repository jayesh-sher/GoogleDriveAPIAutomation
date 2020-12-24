package stepdefinitions;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class DriveAPIAutomation {
	private static final String APP_NAME = "DriveAPIAutomation";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIR = "tokens";
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
	private static final String CRED_DIR = "/credentials.json";
	private static Drive service;
	public static String path = System.getProperty("user.dir") + "/src/main/resources/files";
	public static String download_path = System.getProperty("user.dir") + "/src/main/resources/files/download";

	public DriveAPIAutomation() {
		try {
			initializeDrive();
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		InputStream in = DriveAPIAutomation.class.getResourceAsStream(CRED_DIR);
		if (in == null) {
			throw new FileNotFoundException("Credential file not found: " + CRED_DIR);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIR)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	public void initializeDrive() throws IOException, GeneralSecurityException {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APP_NAME).build();
	}

	public File createFolder(String folder_name) throws IOException {
		File folder = new File();
		folder.setName(folder_name);
		folder.setMimeType("application/vnd.google-apps.folder");
		File folder_drive = service.files().create(folder).setFields("id").execute();
		System.out.println("Folder created with name: " + folder_name + " and ID: " + folder_drive.getId());
		return folder_drive;
	}

	public void deleteFolder(String folder_name) throws IOException {
		FileList result = service.files().list()
				.setQ("mimeType='application/vnd.google-apps.folder' and name='" + folder_name + "'").setSpaces("drive")
				.setFields("nextPageToken, files(id, name)").execute();
		for (File folder : result.getFiles()) {
			service.files().delete(folder.getId()).execute();
		}
		System.out.println("Deleted all folders with name: " + folder_name);
	}

	public File fetchFolder(String folder_name, Boolean createIfNotFound) throws IOException {
		FileList result = service.files().list()
				.setQ("mimeType='application/vnd.google-apps.folder' and name='" + folder_name + "'").setSpaces("drive")
				.setFields("nextPageToken, files(id, name)").execute();
		if (result.getFiles().isEmpty()) {
			System.out.println("No folders found with name: " + folder_name);
			if (createIfNotFound) {
				System.out.println("Proceeding to create new folder");
				return createFolder(folder_name);
			} else {
				return null;
			}

		} else if (result.getFiles().size() > 1) {
			System.out.println("Multiple folders found with name: " + folder_name);
			System.out.println("Proceeding with first folder");
			return result.getFiles().get(0);
		} else {
			System.out.println("Folder found with name: " + folder_name);
			return result.getFiles().get(0);
		}
	}

	public File createJPGFile(String file_name, String folder_name) throws IOException {
		File folder_drive = fetchFolder(folder_name, true);
		File file = new File();
		file.setName(file_name);
		file.setParents(Collections.singletonList(folder_drive.getId()));
		java.io.File filePath = new java.io.File(path + "/" + file_name);
		FileContent mediaContent = new FileContent("image/jpeg", filePath);
		File file_drive = service.files().create(file, mediaContent).setFields("id, parents").execute();
		System.out.println("File created with name: " + file_name + " and ID: " + file_drive.getId());
		return file_drive;
	}

	public void deleteFile(String file_name, String folder_name) throws IOException {
		File folder_drive = fetchFolder(folder_name, false);
		if (folder_drive == null) {
			System.out.println("Folder: " + folder_name + " not found");
		} else {
			FileList result = service.files().list().setQ("mimeType='image/jpeg' and name='" + file_name + "'")
					.setSpaces("drive").setFields("nextPageToken, files(id, name)").execute();
			for (File folder : result.getFiles()) {
				service.files().delete(folder.getId()).execute();
			}
			System.out.println("Deleted all files with name: " + file_name + " in folder: " + folder_name);
		}
	}

	public File fetchFile(String file_name, String folder_name) throws IOException {
		File folder_drive = fetchFolder(folder_name, false);
		if (folder_drive == null) {
			System.out.println("Folder: " + folder_name + " not found");
			return null;
		} else {
			FileList result = service.files().list().setQ("mimeType='image/jpeg' and name='" + file_name + "'")
					.setSpaces("drive").setFields("nextPageToken, files(id, name)").execute();
			if (result.getFiles().isEmpty()) {
				System.out.println("No file found with name: " + file_name);
				return null;
			} else if (result.getFiles().size() > 1) {
				System.out.println("Multiple file found with name: " + file_name);
				System.out.println("Proceeding with first file");
				return result.getFiles().get(0);
			} else {
				System.out.println("File found with name: " + file_name);
				return result.getFiles().get(0);
			}
		}
	}

	public void downloadfile(String file_name, String folder_name) throws IOException {
		OutputStream outputStream = new FileOutputStream(download_path + "/" + "download_" + file_name);
		File file_drive = fetchFile(file_name, folder_name);
		if (file_drive != null) {
			service.files().get(file_drive.getId()).executeMediaAndDownloadTo(outputStream);
		}
	}
}