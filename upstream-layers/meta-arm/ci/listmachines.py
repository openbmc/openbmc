#! /usr/bin/env python3

import pathlib
import typing
import sys

"""
List all of the machines available under the listed sub-layers of meta-arm.
"""
def list_machines(layers: typing.Sequence[str]) -> typing.Set[str]:
    machines = set()

    # We know we're in meta-arm/scripts, so find the top-level directory
    metaarm = pathlib.Path(__file__).resolve().parent.parent
    if metaarm.name != "meta-arm":
        raise Exception("Not running inside meta-arm")
    
    for layer in layers:
        machines |= set(p.stem for p in (metaarm / layer / "conf" / "machine").glob("*.conf"))
    return machines

if __name__ == "__main__":
    if len(sys.argv) > 1:
        machines = list_machines(sys.argv[1:])
        print(" ".join(sorted(machines)))
        sys.exit(0)
    else:
        print("Usage:\n$ %s [layer name ...] " % sys.argv[0])
        sys.exit(1)
