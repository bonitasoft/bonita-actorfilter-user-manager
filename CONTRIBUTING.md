## Contributing

We are pleased to receive any kind of contribution (issues, pull requests, suggestions ...).  

#### Commit message format

Here is the expected commit format, please respect it, we rely on it to [automatically generate the changelog](https://github.com/lob/generate-changelog#usage): 

```
type(category): description [flags]

< commit description >
```

Where  `type`  is one of the following:

-   `breaking`
-   `build`
-   `ci`
-   `chore`
-   `docs`
-   `feat`
-   `fix`
-   `other`
-   `perf`
-   `refactor`
-   `revert`
-   `style`
-   `test`

Where  `flags`  is an optional comma-separated list of one or more of the following (must be surrounded in square brackets):

-   `breaking`: alters  `type`  to be a breaking change

And  `category`  can be anything of your choice. If you use a type not found in the list (but it still follows the same format of the message), it'll be grouped under  `other`.

#### Tests

Ensure that your contribution is correctly tested: 

 - Any update on the generated project must be tested through the generated unit tests
 - Any update on the archetype must be tested through the integration test suite (*src/test/resources/projects*)
 
 
## Bonita actor filter development

The readme contains details on the content of the generated project, and how it should be used to develop and build a Bonita actor filter. More details are available in the documentation: [https://documentation.bonitasoft.com/](https://documentation.bonitasoft.com/).

### Definition
A actor filter is first defined by its **definition**.  It is an XML file located in _src/main/resources/[artifactId].def_ by default.   
A definition defines the inputs of the actor filter. It can be seen as a black box. Then, implementations of this definition can be created, they just need to respect the inputs contract of the definition.  
The connector definition XSD is available in _schemas/connector-definition-descriptor.xsd_, you can import it in a IDE to get completion. 

Example: 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<definition:ConnectorDefinition xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:definition="http://www.bonitasoft.org/ns/connector/definition/6.1">
    <id>myConnector</id> <!-- Id of the definition -->
    <version>1.0.0</version> <!-- Version of the definition -->
    <icon>icon.png</icon> <!-- The icon used in the Studio for this definition -->
    <category icon="icon.png" id="Custom"/> <!-- The category of this definition, used in the Studio (e.g: http, script ...) -->
  
    <!-- Connector inputs -->
    <input mandatory="true" name="defaultInput" type="java.lang.String"/>
    
    <!--
       Pages and widgets to use the connector in the Bonita Studio.
       - Each widget must be bound to an input
       - Page titles must be defined in the properties files
       - Widget labels must be defined in the properties files
       - Page and widget descriptions can be defined in the properties files (optional)
    -->
    <page id="defaultPage">
        <widget xsi:type="definition:Text" id="defaultInputWidget" inputName="defaultInput"/>
    </page>
  
</definition:ConnectorDefinition>
```
### Actor filter Inputs

The inputs of an actor filter are defined in the definition. Those inputs are valued by processes, and are retrieved by the implementation classes of the actor filter to execute the business logic.  
A actor filter input: 

 - Has a name
 - Has a type
 - Has an optional default value
 - Can be mandatory 

### Pages and widgets
A definition includes _pages_ and _widgets_.  Those elements define the UI that will appear in the Bonita Studio to configure the actor filter.  

 - A widget is bound to an input
 - A page contains a set of widgets

The idea is to create pages for related inputs, so the person who will configure the actor filter will easily understand what he has to do.

 All the available widgets are defined in the XSD. You must reference the widget type in the tag to create a specific widget: 

``` xml 
<widget  xsi:type="definition:[WIDGET TYPE]"  id="[WIDGET ID]"  inputName="[CORRESPONDING INPUT]"/>
```

The widget id is used in the _.properties_ files to define and translate the widget name and the widget description.  
The input name is used to bind this widget to one of the connector inputs.  

Some widgets can require additional informations. For example, if you want to create a select widget with a set of item to select, you will have to do something like that: 

``` xml
<widget xsi:type="definition:Select" id="choiceWidget" inputName="choice">
    <items>Choice 1</items>
    <items>Choice 2</items>
    <items>Choice 3</items>
</widget>
```

### Actor filter implementation

An _actor filter implementation_ implements a definition. A definition defines a set of inputs, implementing a definition means use the provided inputs to create the expected list of users ids.  
Several implementations can be created for a given definition.

An actor filter implementation is made of two elements: 
- An xml file used to explicit the definition implemented, the dependencies required and the name of the implementation class
- A set of Java based classes, constituting the implementation sources

#### Implementation XML file

The implementation XML file is located in _src/main/resources/[actor filter name].impl_ by default.  
The connector definition XSD is available in _schemas/connector-implementation-descriptor.xsd_, you can import it in a IDE to get completion. 

Example: 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<implementation:connectorImplementation xmlns:implementation="http://www.bonitasoft.org/ns/connector/implementation/6.0">
  <implementationId>myFilter-impl</implementationId> <!-- Id of the implementation -->
  <implementationVersion>$implementation.version$</implementationVersion> <!-- Version of the implementation, retrieved from the pom.xml at build time -> ${project.version} -->
  <definitionId>myFilter</definitionId> <!-- Id of the definition implemented -->
  <definitionVersion>1.0.0</definitionVersion> <!-- Version of the definition implemented -->
  <implementationClassname>myGroupId.MyFilterImpl</implementationClassname> <!-- Path to the main implementation class -->
  <description>Default actor filter implementation</description>

<!-- Implementation dependencies, retrieved from the pom.xml at build time -->
$Dependencies$

</implementation:connectorImplementation>
```

#### Implementation sources

The implementation sources contain all the logic of the actor filter:

 - The validation of the inputs
 - The execution of the business logic to filter the users for a given actor.

The archetype offers the possibility to generate the default sources in Java, Groovy or Kotlin. The build result will always be a Java archive (jar), no matters the langage selected.

The entry point of the implementation sources must extend the class _`org.bonitasoft.engine.filter.AbstractUserFilter`_.

#### Build an actor filter project

An actor filter project is built using Maven, and especially the [maven assembly plugin](https://maven.apache.org/plugins/maven-assembly-plugin/).   

By default, a zip archives is built containing all the definitions and implementations found in the project.
By importing this archive in a Bonita Studio you will import all the definitions and implementations created in the project
 
To build the actor filter project, type the following command at the root of the project : 
```
./mvnw clean install
```
The built archive can be found in here `target/[artifact id]-[artifact version].zip` after the build.
