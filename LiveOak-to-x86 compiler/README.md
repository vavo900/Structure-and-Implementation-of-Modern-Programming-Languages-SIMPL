
# LiveOak to x86 Compiler

This repository contains the implementation of the LiveOak to x86 Compiler as part of the assignment for C S 395T SIMPL, Fall 2021.

## Introduction

This programming assignment involves retargeting the compiler for LiveOak to generate code for x86 architecture. The project includes emitting x86 code, adjusting call stacks, stack frames, and call linkage, and implementing a basic register allocator.

## Logistics

You may use up to two late (slip) days for this assignment. Please make sure to finish before the due date as extensions will not be granted. The project must be submitted electronically, and testing will be conducted automatically via Gradescope.

## Details of the Assignment

The task is to create a handwritten recursive-descent parser and SaM code generator for Level 2 of the LiveOak language. The previous assignment’s formal grammar of the language is followed, with the exception of the String data type. The SaMTokenizer class is used for lexical analysis.

## Download and Preparation

You will need the following materials:
- SaM Library (v 2.6.3) for lexer
- A collection of public test cases

Ensure your compiler is placed in the `assignment2.LiveOakCompiler` class, and the compiler should accept two command-line arguments: an input LiveOak program file and an output file for the generated x86 assembly code.

## Hand-in Instructions

Submit the following:
1. `compiler.jar`: A runnable JAR file.
2. `source.zip`: A zip file containing all source files.

## Evaluation

Submissions are evaluated using public and private test cases on Gradescope. Ensure your compiler produces correct exit statuses for all test cases.

## Grading

This assignment is auto-graded and contributes 24% to the final course grade. Test cases are categorized by difficulty levels: easy, medium, and hard.
