import java.util.List;
import java.util.Map;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class OllirHelper {

    public static Type determineMethodReturnType(String methodName, MySymbolTable table, JmmNode node)
    {
        Type type = table.getReturnType(methodName);
        if (type != null) return type;
        
        var parentNode = node.getParent();
        String parentNodeKind = parentNode.getKind();

        switch (parentNodeKind)
        {
            case "LessThan":
            case "Add":
            case "Sub":
            case "Mul":
            case "Div": return new Type("int", false);
            case "Neg":
            case "AND": return new Type("boolean", false);
            case "ArrayAccess":
            {
                int childIndex = findIndexOfChild(parentNode, node);
                boolean isArray = false;
                if (childIndex == 0) isArray = true;
                return new Type("int", isArray);
            }
            case "Args":
            {
                var grandparent = parentNode.getParent();
                String chainedMethodName = grandparent.getChildren().get(1).get("name");
                var chainedMethodParams = table.getParameters(chainedMethodName);
                if (chainedMethodParams != null)
                {
                    int childIndex = findIndexOfChild(parentNode, node);
                    return chainedMethodParams.get(childIndex).getType();
                }
            }
            case "Body": return new Type("void", false);
            default: return null;
        }
    }

    public static boolean compareNodes(JmmNode node1, JmmNode node2)
    {
        String kind1, kind2;
        kind1 = node1.getKind();
        kind2 = node2.getKind();
        if (!kind1.equals(kind2)) return false;

        List<String> attributes1 = node1.getAttributes();
        List<String> attributes2 = node2.getAttributes();
        
        if (attributes1.size() != attributes2.size()) return false;

        for (int i = 0; i < attributes1.size(); i++) 
        {
            if (!attributes1.get(i).equals(attributes2.get(i))) return false;
            String attr1 = node1.get(attributes1.get(i));
            String attr2 = node2.get(attributes2.get(i));
            if (!attr1.equals(attr2)) return false;
        }

        int numChildren1 = node1.getNumChildren();
        int numChildren2 = node2.getNumChildren();

        if (numChildren1 != numChildren2) return false;

        return true;
    }

    public static int findIndexOfChild(JmmNode father, JmmNode child)
    {
        var children = father.getChildren();

        for (int i = 0; i < children.size(); i++) {
            if (compareNodes(children.get(i), child)) return i;
        }

        return -1;
    }

    public static boolean determineIfMethodIsStatic(String methodName, MySymbolTable table, JmmNode node)
    {
        if (methodName.equals("main")) return true;

        Type returnType = table.getReturnType(methodName);
        if (returnType != null) return false;

        var parentNode = node.getParent();
        String parentNodeKind = parentNode.getKind();

        if (parentNodeKind.equals("Body")) return true;

        var children = node.getChildren();
        var firstChild = children.get(0);
        String firstChildKind = firstChild.getKind();

        if (firstChildKind.equals("This")) return false;
        else if (firstChildKind.equals("VariableName"))
        {
            String varName = firstChild.get("name");
            Symbol varSymbol = table.getVariable(varName, SearchHelper.getMethodName(node));
            if (varSymbol == null) return true;
        }

        return false;
    }

    public static Type getTypeFromOllir(String declaration)
    {
        boolean isArray = false;
        if (declaration.contains(".array.")) isArray = true;
        int lastDotIndex = declaration.lastIndexOf(".");
        String typeFragment = declaration.substring(lastDotIndex + 1);
        String typeName = "";
        switch (typeFragment)
        {
            case "i32":
            {
                typeName = "int";
                break;
            }
            case "bool":
            {
                typeName = "boolean";
                break;
            }
            case "String":
            {
                typeName = "String";
                break;
            }
            default:
            {
                typeName = typeFragment;
                break;
            }
        }
        return new Type(typeName, isArray);
    }

    public static String trimType(String ollirVarDeclaration)
    {
        String trimmedString = "";
        int lastDotIndex = ollirVarDeclaration.lastIndexOf(".");
        trimmedString = ollirVarDeclaration.substring(0, lastDotIndex);
        return trimmedString;
    }

    public static String extractLastTempVar(String ollirString)
    {
        String stringWithoutNewLines = ollirString.replaceAll("\n", "");
        if (stringWithoutNewLines.endsWith(".V;") && stringWithoutNewLines.contains("invokespecial"))
        {
            String[] lines = stringWithoutNewLines.split(";");
            String secToLastLine = lines[lines.length - 2];
            return secToLastLine.split(":=")[0];
        } 
        if (!stringWithoutNewLines.contains(";")) return stringWithoutNewLines;
        String[] lines = stringWithoutNewLines.split(";");
        String lastLine = lines[lines.length - 1];
        if (!lastLine.contains(":=")) return lastLine;
        String lastTempVar = lastLine.split(":=")[0];
        return lastTempVar.replaceAll("\\s", "");
    }

    public static int determineNumberForStructure(String structure, Map<String, Integer> structureCount)
    {
        Integer currentCount = structureCount.get(structure);
        if (currentCount == null)
        {
            structureCount.put(structure, Integer.valueOf(1));
            return 1;
        }
        else
        {
            Integer updatedCount = currentCount++;
            structureCount.put(structure, updatedCount);
            return updatedCount.intValue();
        }
    }

    public static int lookupVarName(List<Symbol> searchList, String varName)
    {
        for (int i = 0; i < searchList.size(); i++) {
            if (searchList.get(i).getName().equals(varName)) return i;
        }
        return -1;
    }

    public static String processType(Type type)
    {
        String typeString = "";
        String typeName = type.getName();

        if (type.isArray()) typeString += "array.";

        switch (typeName)
        {
            case "int":
            {
                typeString += "i32";
                break;
            }
            case "boolean":
            {
                typeString += "bool";
                break;
            }
            case "String":
            {
                typeString += "String";
                break;
            }
            case "void":
            {
                typeString += "V";
                break;
            }
            default:
            {
                typeString += typeName;
                break;
            }
        }
        return typeString;
    }

    public static String sanitizeVariableName(String varName)
    {
        //TODO 
        //FINISH THIS
        //CHECK FOR $ OR OTHER INVALID CHARS?

        String newVarName = ""; 

        if (varName.matches("t[0-9]+"))
        {
            newVarName = "not_temp_" + varName;
        }
        else newVarName = varName;

        return newVarName;
    }

    public static String extractCode(String pseudoOllirString)
    {
        String ollirString = "";
        
        if (pseudoOllirString.endsWith(";\n")) ollirString = pseudoOllirString;
        else
        {
            String[] lines = pseudoOllirString.split(";\n");
            for (int i = 0; i < (lines.length - 1); i++) {
                ollirString += lines[i] + ";\n";
            }
        }

        return ollirString;
    }
    
}
