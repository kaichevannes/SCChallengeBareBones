package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Main class where execution takes place. */
public class Main {

  /**
   * Main function where execution takes place.
   *
   * @param args no custom args
   * @throws IOException invalid filename
   */
  public static void main(String[] args) throws IOException {
    String filename = "multiply.txt";
    String[] instructionArray = getInstructionArray(filename);
    System.out.println(Arrays.toString(instructionArray));
    List<Variable> variableList = new ArrayList<>();
    // This is not assigned to a variable since the output is only required for while loop
    // recursion.
    executeProgram(instructionArray, variableList, 0);
    System.out.println("*****************************************");
    System.out.println("    Outputting final variable values.    ");
    System.out.println("*****************************************");
    for (Variable variable : variableList) {
      System.out.printf(
          "Variable name: %s | Variable value: %d\n", variable.getName(), variable.getValue());
    }
  }

  /**
   * Executes program and returns variable values.
   *
   * @param instructionArray String array of instructions.
   * @param variableList list of all variables in the current program.
   * @param startIndex index to start execution on.
   * @return -1 if the program has terminated. index of the current end statement otherwise.
   */
  static int executeProgram(
      String[] instructionArray, List<Variable> variableList, int startIndex) {
    String variableName;

    String clearRegex = "(?<=clear )(.*)";
    String incrRegex = "(?<=incr )(.*)";
    String decrRegex = "(?<=decr )(.*)";
    String whileRegex = "(?<=while )(.*)(?= not 0 do)";
    String endRegex = "end";
    String terminationRegex = "_";

    Pattern clearPattern = Pattern.compile(clearRegex);
    Pattern incrPattern = Pattern.compile(incrRegex);
    Pattern decrPattern = Pattern.compile(decrRegex);
    Pattern whilePattern = Pattern.compile(whileRegex);
    Pattern endPattern = Pattern.compile(endRegex);
    Pattern terminationPattern = Pattern.compile(terminationRegex);

    for (int i = startIndex; i < instructionArray.length; i++) {
      Matcher clearMatcher = clearPattern.matcher(instructionArray[i]);
      Matcher incrMatcher = incrPattern.matcher(instructionArray[i]);
      Matcher decrMatcher = decrPattern.matcher(instructionArray[i]);
      Matcher whileMatcher = whilePattern.matcher(instructionArray[i]);
      Matcher endMatcher = endPattern.matcher(instructionArray[i]);
      Matcher terminationMatcher = terminationPattern.matcher(instructionArray[i]);

      // Match keyword and get associated variable.
      if (clearMatcher.find()) {
        variableName = clearMatcher.group();
        clearVariable(variableName, variableList);
      } else if (incrMatcher.find()) {
        variableName = incrMatcher.group();
        incrementVariable(variableName, variableList);
      } else if (decrMatcher.find()) {
        variableName = decrMatcher.group();
        decrementVariable(variableName, variableList);
      } else if (whileMatcher.find()) {
        variableName = whileMatcher.group();
        int endIndex = -1;
        Variable currentVariable = getVariableFromName(variableName, variableList);
        // Recursively call executeProgram.
        while (currentVariable.getValue() != 0) {
          System.out.printf(
              "While Loop: Variable %s value = %d.\n",
              currentVariable.getName(), currentVariable.getValue());
          endIndex = executeProgram(instructionArray, variableList, i + 1);
        }
        // Go to the next instruction after the while loop.
        i = endIndex;
      } else if (endMatcher.find()) {
        // When we get to an end statement return to the while loop recursively.
        return i;
      } else if (terminationMatcher.find()) {
        // When we get to the end of the program break out of the for loop and return.
        break;
      } else {
        System.err.println("Could not match line.");
        System.exit(1);
      }
    }
    return -1;
  }

  /**
   * Clears the variable given, settings its value to 0 and creating it if it does not exist.
   *
   * @param variableName name of the variable to clear.
   * @param variableList list of all variables in the current program.
   */
  static void clearVariable(String variableName, List<Variable> variableList) {
    Variable currentVariable = getVariableFromName(variableName, variableList);

    if (currentVariable != null) {
      System.out.printf("Variable %s exists.\n", variableName);
      System.out.printf("Clearing variable %s.\n", variableName);
      currentVariable.clear();
    } else {
      System.out.printf("Variable %s does not exist.\n", variableName);
      System.out.printf("Creating variable %s.\n", variableName);
      Variable newVar = new Variable(variableName);
      System.out.printf("Clearing variable %s.\n", variableName);
      newVar.clear();
      // This reflects the other variableList since both point to the same data in the heap.
      variableList.add(newVar);
    }
  }

  /**
   * Increment given variable from the list.
   *
   * @param variableName the name of the variable to increment.
   * @param variableList the list of all variables in the current program.
   */
  static void incrementVariable(String variableName, List<Variable> variableList) {
    // currentVariable will never return null in a well formatted program.
    Variable currentVariable = getVariableFromName(variableName, variableList);
    System.out.printf("Incrementing variable %s.\n", variableName);
    assert currentVariable != null;
    currentVariable.incr();
    System.out.printf("Variable %s value is now %d.\n", variableName, currentVariable.getValue());
  }

  /**
   * Decrement given variable from the list.
   *
   * @param variableName the name of the variable to decrement.
   * @param variableList the list of all variables in the current program.
   */
  static void decrementVariable(String variableName, List<Variable> variableList) {
    // currentVariable will never return null in a well formatted program.
    Variable currentVariable = getVariableFromName(variableName, variableList);
    System.out.printf("Decrementing variable %s.\n", variableName);
    assert currentVariable != null;
    currentVariable.decr();
    System.out.printf("Variable %s value is now %d.\n", variableName, currentVariable.getValue());
  }

  /**
   * Gets the associates variable from the given variable name and variable list.
   *
   * @param variableName name of the variable to match.
   * @param variableList list of all variables in the current program.
   * @return Variable object if it exists; null if it does not.
   */
  static Variable getVariableFromName(String variableName, List<Variable> variableList) {
    for (Variable variable : variableList) {
      if (variable.getName().equals(variableName)) {
        return variable;
      }
    }
    return null;
  }

  /**
   * Gets the instruction array from a given text file, splitting lines on ';'.
   *
   * @param filename name of the file to be executed.
   * @return String array of instructions in the file.
   */
  static String[] getInstructionArray(String filename) throws IOException {
    // BufferedReader example referenced from:
    // https://stackoverflow.com/questions/4716503/reading-a-plain-text-file-in-java
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line.trim());
        line = br.readLine();
      }
      sb.append("_");
      String everything = sb.toString();
      return everything.split(";");
    }
  }
}
