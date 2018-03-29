# GatherContacts
A Burp Suite Extension to pull Employee Names from Google and Bing LinkedIn Search Results.

As part of reconnaissance when performing a penetration test, it is often useful to gather employee names that can then be massaged into email addresses and usernames. The usernames may come in handy for performing a [password spraying attack](http://www.blackhillsinfosec.com/?p=4694) for example. One easy way to gather employee names is to use the following Burp Suite Pro extension as described below. You can then massage these employee names into any username format. You may be able to discover the username format by analyzing the metadata of documents posted to a company's public web sites as described [here](https://github.com/dafthack/PowerMeta).
To collect employee names with Burp, you'll need to do the following steps.

## Step 1
Add the "Gather Contacts" extension from the **Extender-->Extension**  tab as shown below

![Gather Contacts Extension](https://github.com/clr2of8/GatherContacts/raw/master/images/AddExtension.png)

Click **Add-->SelectFile** and browse to the "GatherContacts.jar" file that you download from this repository.

## Step 2
This extension uses the *jsoup* Java library. You will need to [download jsoup](https://jsoup.org/download) and tell Burp where to find it as shown below.

![Jsoup Dependency](https://github.com/clr2of8/GatherContacts/raw/master/images/jsoup.png)

Select the folder that contains the jsoup jar file, in this case I download jsoup into the **C:\Users\Public\Downloads\lib** folder.

## Step 3
Configure the Extension to save output to a file. This is where your usernames will be written. You can optionally select the "Show in UI" option, but the output window truncates items when the list gets too long.

![Save Output](https://github.com/clr2of8/GatherContacts/raw/master/images/outputFile.png)

## Step 4
Configure your browser to use Burp as a proxy as you normally would. From the browser, do a Google search of the following form (don't forget the "/in" on the end of "linkedin.com":

site:linkedin.com/in "Company Name"

![Example](https://github.com/clr2of8/GatherContacts/raw/master/images/example.png)

Each of the employee names in the search results will be written to the output file you specified as a tab delimited list. You can click on additional pages of results to get more employee names written to the file.

![Results links](https://github.com/clr2of8/GatherContacts/raw/master/images/google.png)

## Step 5
You can gather a large list of employee names quickly and easily with this method. Try importing the list to Microsoft Excel where you can use formulas to turn employee names into the appropriate username format such as first initial followed by last name.

![Import to Excel](https://github.com/clr2of8/GatherContacts/raw/master/images/excel.png)

## Step 6
When you are done, unload the Extension so you don't burden Burp with inspecting all responses.


