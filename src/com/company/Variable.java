package com.company;

/** A class representing each variable in a "Bare Bones" program. */
public class Variable {
  private String name;
  private int value;

  public Variable(String name) {
    this.name = name;
  }

  /** Clears the value of a variable instance. */
  public void clear() {
    this.value = 0;
  }

  /** Increments the value of a variable instance. */
  public void incr() {
    this.value++;
  }

  /** Decrements the value of a variable instance. */
  public void decr() {
    this.value--;
  }

  /**
   * variableName setter
   *
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * variableName getter
   *
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * variableValue getter
   *
   * @return
   */
  public int getValue() {
    return value;
  }
}
