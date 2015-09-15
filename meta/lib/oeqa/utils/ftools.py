import os
import re

def write_file(path, data):
    wdata = data.rstrip() + "\n"
    with open(path, "w") as f:
        f.write(wdata)

def append_file(path, data):
    wdata = data.rstrip() + "\n"
    with open(path, "a") as f:
            f.write(wdata)

def read_file(path):
    data = None
    with open(path) as f:
        data = f.read()
    return data

def remove_from_file(path, data):
    lines = read_file(path).splitlines()
    rmdata = data.strip().splitlines()
    for l in rmdata:
        for c in range(0, lines.count(l)):
            i = lines.index(l)
            del(lines[i])
    write_file(path, "\n".join(lines))
