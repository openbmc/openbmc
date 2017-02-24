# Copyright (c) 2016, Intel Corporation.
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
"""Build performance tests"""
from .base import (BuildPerfTestCase,
                   BuildPerfTestLoader,
                   BuildPerfTestResult,
                   BuildPerfTestRunner,
                   KernelDropCaches,
                   runCmd2)
from .test_basic import *
