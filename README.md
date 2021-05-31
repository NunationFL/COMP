# Group and Project Info

**GROUP**: 2C

NAME1: João Pedro Fontes Vilhena e Mascarenhas, NR1: 201806389, GRADE1:17, CONTRIBUTION1:17%  
NAME2: João Pereira da Silva Matos, NR2: 201703884, GRADE2:, CONTRIBUTION2:  
NAME3: Luís Miguel Afonso Pinto, NR3: 201806206, GRADE3:18.5, CONTRIBUTION3: 25%  
NAME4: Nuno Filipe Amaral Oliveira, NR4: 201806525, GRADE4:18.5, CONTRIBUTION4: 25% 

GLOBAL Grade of the project:18.5 

**SUMMARY**: Our tools allows a user to compile source code files written in the Jmm language and generate ``.class`` files that can be executed. A path to the Jmm file must be provided as a command line argument. Additionally, a ``-o`` option can be provided to apply the corresponding optimizations before converting the syntax tree to OLLIR.

**DEALING WITH SYNTACTIC ERRORS**: The tool tolerates up to 10 syntactic errors in the expressions of ``while`` loops and crashes if errors exist elsewhere or if this limit is exceeded.

**SEMANTIC ANALYSIS**: In this stage, all the rules mentioned in the checklist provided for the second checkpoint are implemented, this includes:
### Expression Analysis 

#### Type Verification
- verificar se operações são efetuadas com o mesmo tipo (e.g. int + boolean tem de dar erro)
- não é possível utilizar arrays diretamente para operações aritmeticas (e.g. array1 + array2)
- verificar se um array access é de facto feito sobre um array (e.g. 1[10] não é permitido)
- verificar se o indice do array access é um inteiro (e.g. a[true] não é permitido)
- verificar se valor do assignee é igual ao do assigned (a_int = b_boolean não é permitido!)
- verificar se operação booleana (&&, < ou !) é efetuada só com booleanos
- verificar se conditional expressions (if e while) resulta num booleano
            
#### Method Verification

- verificar se o "target" do método existe, e se este contém o método (e.g. a.foo, ver se 'a' existe e se tem um método 'foo')		
    - caso seja do tipo da classe declarada (e.g. a usar o this), se não existir declaração na própria classe: se não tiver extends retorna erro, se tiver extends assumir que é da classe super.	
- caso o método não seja da classe declarada, isto é uma classe importada, assumir como existente e assumir tipos esperados. (e.g. a = Foo.b(), se a é um inteiro, e Foo é uma classe importada, assumir que o método b é estático (pois estamos a aceder a uma método diretamente da classe), que não tem argumentos e que retorna um inteiro)
- verificar se o número de argumentos na invocação é igual ao número de parâmetros da declaração
- verificar se o tipo dos parâmetros coincide com o tipo dos argumentos

**CODE GENERATION**: At this point, the entire tree is converted to OLLIR (OO-based Low Lever Intermediate Representation), also if the ``-o`` option was provided as a command line argument the corresponding optimizations are applied to the tree before the conversion. Finally, the OLLIR code is converted to the corresponding Jasmin code which is then used to generate the ``.class`` file. In the project's current state we were unable to find problems related to code generation.

**TASK DISTRIBUTION**:
- Parsing and syntactic analysis:
    - João Mascarenhas
    - João Matos
    - Luís Pinto
    - Nuno Oliveira
- Semantic analysis:
    - João Mascarenhas
    - João Matos
    - Luís Pinto
    - Nuno Oliveira
- OLLIR:
    - João Matos
- Jasmin:
    - Luís Pinto
    - Nuno Oliveira

**PROS**: Almost everything that was requested in checklists and project instructions was implemented.

**CONS**: Unfortunately, we only had time to implement the optimizations related to the ``-o`` option.

# Compilers Project

For this project, you need to [install Gradle](https://gradle.org/install/)

## Project setup

Copy your ``.jjt`` file to the ``javacc`` folder. If you change any of the classes generated by ``jjtree`` or ``javacc``, you also need to copy them to the ``javacc`` folder.

Copy your source files to the ``src`` folder, and your JUnit test files to the ``test`` folder.

## Compile

To compile the program, run ``gradle build``. This will compile your classes to ``classes/main/java`` and copy the JAR file to the root directory. The JAR file will have the same name as the repository folder.

### Run

To run you have two options: Run the ``.class`` files or run the JAR.

### Run ``.class``

To run the ``.class`` files, do the following:

```cmd
java -cp "./build/classes/java/main/" <class_name> <arguments>
```

Where ``<class_name>`` is the name of the class you want to run and ``<arguments>`` are the arguments to be passed to ``main()``.

### Run ``.jar``

To run the JAR, do the following command:

```cmd
java -jar <jar filename> <arguments>
```

Where ``<jar filename>`` is the name of the JAR file that has been copied to the root folder, and ``<arguments>`` are the arguments to be passed to ``main()``.

## Test

To test the program, run ``gradle test``. This will execute the build, and run the JUnit tests in the ``test`` folder. If you want to see output printed during the tests, use the flag ``-i`` (i.e., ``gradle test -i``).
You can also see a test report by opening ``build/reports/tests/test/index.html``.

## Checkpoint 1
For the first checkpoint the following is required:

1. Convert the provided e-BNF grammar into JavaCC grammar format in a .jj file
2. Resolve grammar conflicts (projects with global LOOKAHEAD > 1 will have a penalty)
3. Proceed with error treatment and recovery mechanisms for the while expression
4. Convert the .jj file into a .jjt file
5. Include missing information in nodes (i.e. tree annotation). E.g. include class name in the class Node.
6. Generate a JSON from the AST

### JavaCC to JSON
To help converting the JavaCC nodes into a JSON format, we included in this project the JmmNode interface, which can be seen in ``src-lib/pt/up/fe/comp/jmm/JmmNode.java``. The idea is for you to use this interface along with your SimpleNode class. Then, one can easily convert the JmmNode into a JSON string by invoking the method JmmNode.toJson().

Please check the SimpleNode included in this repository to see an example of how the interface can be implemented, which implements all methods except for the ones related to node attributes. How you should store the attributes in the node is left as an exercise.

### Reports
We also included in this project the class ``src-lib/pt/up/fe/comp/jmm/report/Report.java``. This class is used to generate important reports, including error and warning messages, but also can be used to include debugging and logging information. E.g. When you want to generate an error, create a new Report with the ``Error`` type and provide the stage in which the error occurred.


### Parser Interface

We have included the interface ``src-lib/pt/up/fe/comp/jmm/JmmParser.java``, which you should implement in a class that has a constructor with no parameters (please check ``src/Main.java`` for an example). This class will be used to test your parser. The interface has a single method, ``parse``, which receives a String with the code to parse, and returns a JmmParserResult instance. This instance contains the root node of your AST, as well as a List of Report instances that you collected during parsing.

To configure the name of the class that implements the JmmParser interface, use the file ``parser.properties``.
