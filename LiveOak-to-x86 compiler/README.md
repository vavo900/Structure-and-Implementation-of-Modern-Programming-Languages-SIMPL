
# LiveOak to x86 Compiler

This repository contains the implementation of the LiveOak to x86 Compiler as part of the assignment for C S 395T SIMPL, Fall 2021.

## Introduction

This programming assignment involves retargeting the compiler for LiveOak to generate code for x86 architecture. 
The project includes:
- Emitting x86 code
- Adjusting call stacks, stack frames, and call linkage
- Implementing a basic register allocator.

## Logistics

- You may use up to **two late (slip) days** for this assignment.
- No extensions will be granted.
- Testing will be conducted automatically via **Gradescope**.
- Hand-ins are **electronic**.

## Details of the Assignment

### Task
Create a handwritten **recursive-descent parser** and **SaM code generator** for Level 2 of the LiveOak language. 
Refer to the previous assignment for the formal grammar of the language, but ignore the String data type.

- Use the provided `SaMTokenizer` class for lexical analysis.
- The compiler should take an input LiveOak program and produce an x86 assembly output.

### Command-line Arguments
The compiler should accept two arguments:
1. An input file containing a LiveOak program.
2. An output file to store the generated x86 assembly code.

```bash
java -jar compiler.jar input_file.lo output_file.s
```

## Download and Preparation

1. **SaM Library** (v 2.6.3) for the lexer can be downloaded from the provided links.
2. **Public test cases** for the compiler are available for download. The test cases are continuously updated.
   - It is recommended to create additional test cases to verify your compiler.

## Evaluation

The following steps are used to evaluate your submission:

1. **Compiling the LiveOak program**:
```bash
java -jar compiler.jar test1.lo output.s
```
This command reads the LiveOak program from `test1.lo`, generates the SaM program, and outputs it to `output.s`.

2. **Running the x86 program**:
   Use SASM to simulate the execution of the generated x86 code. 
   The exit status (return value of `main()`) will be used to evaluate the correctness of your compiler.

## Hand-in Instructions

Submit the following via **Gradescope**:
1. `compiler.jar`: A runnable JAR file.
2. `source.zip`: A ZIP file containing all your source files, including any libraries you used.

Ensure that your submission can be run with the command mentioned in the **Evaluation** section.

## Grading

This assignment contributes **24%** to the final course grade. 
Test cases are assigned difficulty levels:
- Easy: 3 points
- Medium: 5 points
- Hard: 7 points

You are strongly encouraged to create additional test cases beyond the provided public ones to ensure comprehensive testing.
