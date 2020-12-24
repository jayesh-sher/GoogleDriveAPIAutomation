@GoogleDriveAPIAutomation 
Feature: GoogleDrive_APIAutomation 

#default user is "testgdrive77" and password is "test@131"
#all files upload from "src/main/resources/files" folder
#all files download to "src/main/resources/files/download" folder
#use "src/main/java/runner/TestRunner" to run this feature

Background: 
	Given Drive account user is authorized 
	
Scenario Outline: Creating folder 
	When Folder <folder_name> is not present 
	Then Create folder <folder_name> on drive 
	Examples: 
		|folder_name|
		|folder1	|
		|folder2    |		
		
Scenario Outline: Creating file in folder 
	When File <file_name> is not present in the folder <folder_name> 
	Then Create file <file_name> in folder <folder_name> on drive 
	Examples: 
		|file_name|folder_name|
		|file1.jpg|folder1    |
		|file2.jpg|folder2    |
		
Scenario Outline: Downloading file from folder 
	When File <file_name> is present in the folder <folder_name> 
	Then Download file <file_name> from folder <folder_name> on drive 
	Examples: 
		|file_name|folder_name|
		|file1.jpg|folder1    |
		|file2.jpg|folder2    |
		
Scenario Outline: Deleting file in folder 
	When File <file_name> is present in the folder <folder_name> 
	Then Delete file <file_name> from folder <folder_name> on drive 
	Examples: 
		|file_name|folder_name|
		|file1.jpg|folder1    |
		|file2.jpg|folder2    |
		
Scenario Outline: Delete folder 
	When Folder <folder_name> is present 
	Then Delete folder <folder_name> from drive 
	Examples: 
		|folder_name|
		|folder1	|
		|folder2    |		
		
		
		
		
