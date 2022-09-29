#! /usr/bin/env python3

"""
Print an overview of the layer to help writing release notes.

Output includes sublayers, machines, recipes.
"""

import argparse
import sys

# TODO:
# - More human-readable output
# - Diff mode, give two revisions and list the changes

def is_layer(path):
    """
    Determine if this path looks like a layer (is a directory and contains conf/layer.conf).
    """
    return path.is_dir() and (path / "conf" / "layer.conf").exists()


def print_layer(layer):
    """
    Print a summary of the layer.
    """
    print(layer.name)

    machines = sorted(p for p in layer.glob("conf/machine/*.conf"))
    if machines:
        print(" Machines")
        for m in machines:
            print(f"  {m.stem}")
        print()

    recipes = sorted((p for p in layer.glob("recipes-*/*/*.bb")), key=lambda p:p.name)
    if recipes:
        print(" Recipes")
        for r in recipes:
            if "_" in r.stem:
                pn, pv = r.stem.rsplit("_", 1)
                print(f"  {pn} {pv}")
            else:
                print(f"  {r.stem}")
        print()


parser = argparse.ArgumentParser()
parser.add_argument("repository")
parser.add_argument("revision", nargs="?")
args = parser.parse_args()

if args.revision:
    import gitpathlib
    base = gitpathlib.GitPath(args.repository, args.revision)
else:
    import pathlib
    base = pathlib.Path(args.repository)

if is_layer(base):
    print_layer(base)
else:
    sublayers = sorted(p for p in base.glob("meta-*") if is_layer(p))
    if sublayers:
        print("Sub-Layers")
        for l in sublayers:
            print(f" {l.name}")
        print()

        for layer in sublayers:
            print_layer(layer)
    else:
        print(f"No layers found in {base}", file=sys.stderr)
        sys.exit(1)
