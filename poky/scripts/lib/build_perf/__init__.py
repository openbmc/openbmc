#
# Copyright (c) 2017, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
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

