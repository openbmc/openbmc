#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os
import sys

def findFile(file_name, directory):
    """
        Search for a file in directory and returns its complete path.
    """
    for r, d, f in os.walk(directory):
        if file_name in f:
            return os.path.join(r, file_name)
    return None

def remove_safe(path):
    if os.path.exists(path):
        os.remove(path)

