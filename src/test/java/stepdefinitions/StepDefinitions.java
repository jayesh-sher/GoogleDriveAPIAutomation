package stepdefinitions;

import java.io.File;
import java.io.IOException;

import io.cucumber.java.en.*;
import setup.DriveAPIAutomation;

import org.junit.Assert;

public class StepDefinitions {

	DriveAPIAutomation drive = new DriveAPIAutomation();

	@Given("Drive account user is authorized")
	public void drive_account_user_is_authorized() throws IOException {
		File cred = new File(System.getProperty("user.dir") + "/tokens/StoredCredential");
		if (!(cred.exists())) {
			System.out.println(
					"Dummy user not authorized, please use the following credentials one time when prompted on browser and press enter");
			System.out.println("User: testgdrive77");
			System.out.println("Password: test@131");
			System.in.read();
		}
	}

	@When("Folder {} is not present")
	public void folder_is_not_present(String folder_name) throws IOException {
		drive.deleteFolder(folder_name);
		Assert.assertTrue("Folder is not deleted", drive.fetchFolder(folder_name, false) == null);
	}

	@Then("Create folder {} on drive")
	public void create_folder_on_drive(String folder_name) throws IOException {
		drive.createFolder(folder_name);
		Assert.assertTrue("Folder is not created", drive.fetchFolder(folder_name, false) != null);
	}

	@When("File {} is not present in the folder {}")
	public void file_is_not_present_in_the_folder(String file_name, String folder_name) throws IOException {
		drive.deleteFile(file_name, folder_name);
		Assert.assertTrue("File is not deleted", drive.fetchFile(file_name, folder_name) == null);
	}

	@Then("Create file {} in folder {} on drive")
	public void create_file_in_folder_on_drive(String file_name, String folder_name) throws IOException {
		drive.createJPGFile(file_name, folder_name);
		Assert.assertTrue("File is not created", drive.fetchFile(file_name, folder_name) != null);
	}

	@When("File {} is present in the folder {}")
	public void file_is_present_in_the_folder(String file_name, String folder_name) throws IOException {
		if (drive.fetchFile(file_name, folder_name) == null) {
			drive.createJPGFile(file_name, folder_name);
			Assert.assertTrue("File is not created", drive.fetchFile(file_name, folder_name) != null);
		}

	}

	@Then("Download file {} from folder {} on drive")
	public void download_file_from_drive(String file_name, String folder_name)
			throws IOException, InterruptedException {
		File file = new File(DriveAPIAutomation.download_path + "/" + "download_" + file_name);
		file.delete();
		Assert.assertFalse("Download cleanup failed", file.exists());
		drive.downloadfile(file_name, folder_name);
		Assert.assertTrue("File not downloaded", file.exists());
	}

	@Then("Delete file {} from folder {} on drive")
	public void delete_file_from_folder_on_drive(String file_name, String folder_name) throws IOException {
		drive.deleteFile(file_name, folder_name);
		Assert.assertTrue("File is not deleted", drive.fetchFile(file_name, folder_name) == null);
	}

	@When("Folder {} is present")
	public void folder_is_present(String folder_name) throws IOException {
		Assert.assertTrue("Folder is not created", drive.fetchFolder(folder_name, true) != null);
	}

	@Then("Delete folder {} from drive")
	public void delete_folder_from_drive(String folder_name) throws IOException {
		drive.deleteFolder(folder_name);
		Assert.assertTrue("Folder is not deleted", drive.fetchFolder(folder_name, false) == null);
	}

}
