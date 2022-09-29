#! /usr/bin/env python3

import argparse
import sys

def do_test_parameters(args):
    if not args.parameter or set(args.parameter) != set(("board.cow=moo", "board.dog=woof")):
        print(f"Unexpected arguments: {args}")
        sys.exit(1)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("-C", "--parameter", action="append")
    args = parser.parse_args()

    function = "do_" + parser.prog.replace("-", "_").replace(".py", "")
    if function in locals():
        locals()[function](args)
    else:
        print(f"Unknown mock mode {parser.prog}")
        sys.exit(1)
