#
# Copyright (c) 2017, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Helper module for HTML reporting"""
from jinja2 import Environment, PackageLoader


env = Environment(loader=PackageLoader('build_perf', 'html'))

template = env.get_template('report.html')
