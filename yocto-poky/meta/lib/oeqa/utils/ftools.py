import os
import re
import errno

def write_file(path, data):
    # In case data is None, return immediately
    if data is None:
        return
    wdata = data.rstrip() + "\n"
    with open(path, "w") as f:
        f.write(wdata)

def append_file(path, data):
    # In case data is None, return immediately
    if data is None:
        return
    wdata = data.rstrip() + "\n"
    with open(path, "a") as f:
            f.write(wdata)

def read_file(path):
    data = None
    with open(path) as f:
        data = f.read()
    return data

def remove_from_file(path, data):
    # In case data is None, return immediately
    if data is None:
        return
    try:
        rdata = read_file(path)
    except IOError as e:
        # if file does not exit, just quit, otherwise raise an exception
        if e.errno == errno.ENOENT:
            return
        else:
            raise
    lines = rdata.splitlines()
    rmdata = data.strip().splitlines()
    for l in rmdata:
        for c in range(0, lines.count(l)):
            i = lines.index(l)
            del(lines[i])
    write_file(path, "\n".join(lines))
