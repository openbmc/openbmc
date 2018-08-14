#
# Copyright (c) 2017, Intel Corporation.
#
# This program is free software; you can redistribute it and/or modify it
# under the terms and conditions of the GNU General Public License,
# version 2, as published by the Free Software Foundation.
#
# This program is distributed in the hope it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
# more details.
#
"""Build performance test library functions"""

def print_table(rows, row_fmt=None):
    """Print data table"""
    if not rows:
        return
    if not row_fmt:
        row_fmt = ['{:{wid}} '] * len(rows[0])

    # Go through the data to get maximum cell widths
    num_cols = len(row_fmt)
    col_widths = [0] * num_cols
    for row in rows:
        for i, val in enumerate(row):
            col_widths[i] = max(col_widths[i], len(str(val)))

    for row in rows:
        print(*[row_fmt[i].format(col, wid=col_widths[i]) for i, col in enumerate(row)])

