---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# ClientConnect Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

ClientConnect is an AddressBook which is built upon [**AddressBook3**](https://github.com/se-edu/addressbook-level3/) <br>
The app utilizes the following third-party libraries:
* **JavaFX**: ClientConnect's main GUI library
* **JUnit**: Tests functional code
* **Jackson**: Stores data on local machine
With the help of Generative AI to improve the quality of test codes and JavaDoc comments.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2425S2-CS2103T-W13-2/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2425S2-CS2103T-W13-2/tp/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point).

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2425S2-CS2103T-W13-2/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `ClientListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2425S2-CS2103T-W13-2/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2425S2-CS2103T-W13-2/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Client` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2425S2-CS2103T-W13-2/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
2. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is then executed by the `LogicManager`.
3. During execution, the command may interact with the `Model` to perform necessary operations (e.g. deleting a client). While this is shown as a single step in the diagram above for simplicity, it often involves multiple method calls between the `Command` and `Model`.
4. The result of the command execution is encapsulated in a `CommandResult` object which is returned from `Logic`.

Internally, `LogicManager` also handles logging and error tracking through the `LogsCenter` utility. This enables detailed logs for actions such as the input command string and any exceptions thrown during parsing or execution, providing transparency and easing debugging.

The `Logic` component is designed with extensibility in mind. To support a new command, developers simply need to:
- Implement a new subclass of `Command` (e.g., `SortCommand`)
- Create a matching parser (e.g., `SortCommandParser`) that implements the `Parser<T>` interface
- Add a corresponding `case` to the `AddressBookParser#parseCommand` method

This modular design makes it straightforward to extend the system while maintaining clarity and separation of concerns that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2425S2-CS2103T-W13-2/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="500" />


The `Model` component,

* stores the address book data i.e., all `Client` objects (which are contained in a `UniqueClientList` object).
* stores the currently 'selected' `Client` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Client>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Client` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Client` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="475" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2425S2-CS2103T-W13-2/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Rank feature

#### Command Architecture

<puml src="diagrams/RankClassDiagram.puml" width="550" />

The `Rank` command component,
* holds a client `Comparator` that determines the sort order between any two clients using the `compare` method.
* each `Comparator` has a `COMPARATOR_WORD` `String` that is used to parse user input to create the suitable `Comparator`.
* each `Comparator` also depends on the `Client` which helps to gain access to the required parameters to compare with.

#### Implementation

Given below is an example usage scenario and how the rank mechanism behaves at each step.

* The user launches the application, and decides that they want to see `Alice` at the top of their client list.
* They would simply have to type `rank name` as the command input.

Here's the sequence diagram that shows the flow of the execution for the command `rank name`.

<puml src="diagrams/RankSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `rank name` Command" />

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* salespeople who wish to know their clients' preferences better
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: provide fast access to clients’ addresses and preferences to help salespeople in making sales decisions and building rapports with clients, which can potentially increase their sales revenue


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                               | I want to …​                                                             | So that I can…​                                                                |
|----------|-------------------------------------------------------|--------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| `* * *`  | salesperson                                           | add in my clients’ contact details                                       | contact them for business purposes                                             |
| `* * *`  | salesperson                                           | edit my clients’ contact details                                         | keep their information updated and relevant                                    |
| `* * *`  | salesperson                                           | delete a client                                                          | get rid of contact information that I don’t need anymore                       |
| `* * *`  | salesperson                                           | find my clients’ info quickly in a large database                        | locate contact details of clients without having to go through the entire list |
| `* * *`  | salesperson who wants to know my customer preferences | rank my clients based on the most purchased product type                 | find out who my potential customers are for a given product                    |
| `* * *`  | salesperson                                           | record clients' frequency of purchase                                    | personalise advertisement and services                                         |
| `* * *`  | salesperson                                           | record clients' product preferences                                      | promote new products to clients that are more likely to be interested in       |
| `* * *`  | new user                                              | view examples for the main features                                      | get on board with the application more easily                                  |
| `* *`    | user                                                  | be able to expand out the client information                             | view it with a bigger window                                                   |
| `* *`    | user                                                  | undo all my previously executed commands                                 | revert changes without having to manually type in a long command               |
| `* *`    | user                                                  | search for clients by name, phone number, or tag                         | quickly retrieve their information without scrolling through the entire list   |
| `* *`    | salesperson                                           | add profile pictures for my clients                                      | quickly recognize them and personalize my interactions                         |
| `* *`    | salesperson                                           | add social media links to client profiles                                | easily connect with them on different platforms                                |
| `* *`    | salesperson whose clients are of different statuses   | categorize clients into different groups (e.g., VIP, Potential, Regular) | manage them more efficiently and tailor my communication                       |
| `* *`    | salesperson whose customers come from many industries | add tags for clients                                                     | split them into categories                                                     |
| `* *`    | salesperson                                           | filter clients by tags                                                   | quickly see all the clients inside one category                                |
| `* *`    | user                                                  | be able to use the software in dark mode                                 | view the contents comfortably                                                  |
| `*`      | user                                                  | have multiple accounts                                                   | save customer profiles for different businesses                                |
| `*`      | salesperson who needs to report to my company         | export my client list to a CSV file                                      | share it with my team or use it for data analysis and reporting                |
| `*`      | salesperson                                           | track the geographical location of my clients                            | plan visits efficiently                                                        |
| `*`      | paranoid user                                         | access my address book via an authentication system                      | my customer profiles are kept confidential                                     |
| `*`      | user                                                  | use the software in my desired language                                  | understand how to use the software better                                      |


*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `ClientConnect` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Adding a client**

**MSS**

1.  User enters the command to add a client.
2.  ClientConnect validates the input format and checks that all required fields are present.
3.  ClientConnect adds the client to the address book.
4.  ClientConnect displays a confirmation message showing the newly added contact details.


    Use case ends.

**Extensions**

* 2a. Invalid Field Detected:
  
  * 2a1. If ClientConnect detects a missing or incorrectly formatted field, it displays an error message. 
  * 2a2. User re-enters the correct information. 
  * 2a3. ClientConnect re-validates the updated input and proceeds from step 2 once the error is resolved.

**Use case: View List of Clients**

**MSS**

1.  User enters the command to view current list of added entities 
2.  ClientConnect validates the command input. 
3.  ClientConnect retrieves all stored entities from the address book. 
4.  ClientConnect displays the list with each entity’s name, contact number, and address in a clear, formatted view.


    Use case ends.

**Extensions**

* 2a: If the command input is not recognized (e.g., due to a typo), ClientConnect displays: “I do not understand your command, please try again.”
* 3a: If no entities are found (i.e., the address book is empty), ClientConnect displays the message: “Your list is empty.”

**Use case: Delete a client**

**MSS**

1.  User enters the command to view the list of clients. 
2.  ClientConnect displays the full list of clients. 
3.  User enters the command to delete a client. 
4.  ClientConnect validates the command. 
5.  ClientConnect retrieves the details of the client at the specified index. 
6.  ClientConnect deletes the client's details from the address book. 
7.  ClientConnect displays a success message: “Deleted: &lt;Info of the deleted client&gt;”


    Use case ends.

**Extensions**

* 4a: If the <index> parameter is missing or not an integer, ClientConnect displays: “Please provide a valid index for the entry.”
* 4a: If the command includes extra parameters beyond the required <index>, ClientConnect ignores the additional fields and processes the command based solely on the valid <index>.
* 5a: If the provided <index> is not within the range of the list size, ClientConnect displays: “Please provide a valid index for the entry.”
* 5a: If the address book is empty when the command is executed, ClientConnect displays an error message: “Your list is empty.”

**Use case: find a client**

**MSS**

1.  User enters the command to find a list of clients that contains the keyword.
2.  ClientConnect validates the command.
3.  ClientConnect retrieves the information of clients that contains that specified keyword in their descriptions.
4.  ClientConnect displays the list of clients.


    Use case ends.

**Extensions**

* 2a: If the command input is not recognised, ClientConnect displays: “I do not understand your command, please try again.”
* 3a: If there is no client found with <keyword>, ClientConnect displays: "No client found with <keyword>."

**Use case: Describe a client**

**MSS**

1.  User enters command to add more description about the client.
2.  ClientConnect validates the command.
3.  ClientConnect updates the specified client's description.
4.  ClientConnect displays success message with updated client details.

**Extensions**

* 2a. Invalid index provided:
  * 2a1. ClientConnect shows "Invalid index" error.
* 2b. No description provided:
  * 2b1. ClientConnect clears existing description for the client.

  **Use case: Rank clients**

**MSS**

1.  User enters command to rank clients based on a certain criteria.
2.  ClientConnect validates the command.
3.  ClientConnect sorts current client list using specified comparator.
4.  ClientConnect displays ranked list with success message.

**Extensions**

* 2a. Invalid criteria provided:
  * 2a1. ClientConnect shows "Unknown ranking criteria" error.

**Use case: Expand client view**

**MSS**

1.  User enters command to view a specific client's details.
2.  ClientConnect validates the command.
3.  ClientConnect retrieves full details of specified client.
4.  ClientConnect displays expanded view in separate window.

**Extensions**

* 2a. Invalid index provided:
  * 2a1. ClientConnect shows "Invalid index" error.

**Use case: Filter clients**

**MSS**

1.  User enters command to filter clients based on priority level or product preference.
2.  ClientConnect validates filter parameters.
3.  ClientConnect applies filter to current client list.
4.  ClientConnect displays filtered results with success message.

**Extensions**

* 2a. Invalid priority level:
  * 2a1. ClientConnect shows "Invalid priority level" error.
* 2b. Unknown product preference:
  * 2b1. ClientConnect shows "No clients found" message.

**Use case: Get help**

**MSS**

1.  User enters does not understand how to operate the software.
2.  User typed the command to get guidance.
3.  ClientConnect opens help window with command summary.
4.  User copies URL to documentation using copy button.

**Use case: Exit application**

**MSS**

1.  User finished using the product and wished to leave.
2.  User enters command.
3.  ClientConnect saves all data.
4.  ClientConnect closes all windows and terminates.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 clients without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  The user interface should be minimalist and intuitive so that even users who are not tech-savvy can use with ease.
5.  All operations should take less than 1s to finish and display the results.
6.  Should be stable and handles errors gracefully, and minimize data loss in case of failures.

*{More to be added}*

### Glossary

* **Client**: The customers who the salesperson (also user of this application) is selling products to.
* **Command**: A one-line instruction typed into the application by user.
* **Contact detail**: Including contact names, phone numbers, emails, addresses and optionally tags, etc.
* **CSV file**: Comma-separated values, a plain text file format that stores data in a table-like structure. Can be opened using Microsoft Excel or be imported to other supported applications.
* **Main features**: The main features of this application, including _list_, _add_, _delete_, etc.
* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Salesperson**: Anyone who is selling things and has the need to note down clients' information.
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Product Preference**: The most purchased product of the client of interest.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

<box type="tip" seamless>

ClientConnect will **autoload a sample address book with a few entries** to play around with ***if there isn't one being created yet***. So you may ***perform these test cases even without adding new entries***.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### Deleting a client

1. Deleting a client while all clients are being shown

   1. Prerequisites: List all clients using the `list` command. Multiple clients in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No client is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

### Filtering a client's product preference or priority

1. Filtering a client while all clients are being shown.
    1. Prerequisites: List all clients using the `list` command. Multiple clients in the list.

    1. Test case: `filter pref/shampoo`
       Expected: All clients with `shampoo` as part of their product preference are shown.

    1. Test case: `filter priority/2`
       Expected: All clients with the `PREMIUM` priority are shown.

    1. Test case: `filter name/John`
       Expected: Error details shown in the status message.

    1. Other incorrect filter command keywords to try: `filter`, `filter address/`, `...` (where keyword is invalid)
       Expected: Similar to previous.

### Ranking the clients

1. Ranking with all the clients being shown.
   1. Prerequisites: List all clients using the `list` command. Multiple clients in the list.

   1. Test case: `rank name`
      Expected: All clients are sorted based on their names in lexicographical order.
   
    1. Test case: `rank total`
      Expected: All clients are sorted based on their total purchase in descending order.

   1. Other correct rank command keywords to try: `rank toTal`, `rank NAME`
      Expected: Similar to previous.

   1. Test case: `rank handsomeness`
      Expected: Current clients' order does not change. Error details shown in the status message.


--------------------------------------------------------------------------------------------------------------------

## **Appendix: Planned Enhancements**

_Team size: 5_

1. **Support multiple product preferences for each client**: The current client only has one product preference and its frequency. We plan to make every client has a list of product preferences with its corresponding frequency, to meet a more realistic scenario.

2. **New commands to work with product preference**: The current app only provides some basic ways to work with product preference, which can be inconvinient at times. We plan to add more commands such as `AddProductPreference` to add product preferences cumulatively for one client, `DeleteProductPreference` to delete a certain product preference, and `EditProductPreference` to conveniently edit a product preference or its frequency for one client.

3. **New commands to add tags cumulatively**: For the same reason, now we can only edit tags through `edit` command, and if we want to add a new tag to a client without touching existing tags, we will also have to type out all the existing tags in the command, which is very inconvenient and unpractical. We can add new command `AddTag` to add new tags cumulatively to a client, and another new command `DeleteTag` to delete a certain existing tag for a client.

4. **Rank clients by product frequency of a certain product preference**: We want our `rank` command to be more useful, adding its ability to rank in many ways, including rank clients based on the frequency of a certain product.

5. **Search by Contact Information**: Our app currently has no way of finding a client by his contact details. We plan to improve our find feature to allow users to directly search for a client using their phone number or email address for quicker lookup.

6. **Implement Robust Contact Information Verification**: Our app currently only imposes basic rules on contact details like phones and emails. We plan to incorporate third-party APIs to send verification codes to phone numbers and email addresses upon entry, confirming their validity and reachability.

7. **Flexible Client Creation**: The app currently requires all fields for a client to be added, which can be inconvenient or irrelevant at times. We plan to modify the client addition process to enhance user convenience. Instead of requiring all fields (name, phone, email, address), we require only one of these primary contact details to create a new client entry.

