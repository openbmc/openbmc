#!/usr/bin/env python
import re
import sys

import sphinx
from sphinx.application import Sphinx

# This extension uses pyyaml, report an explicit
# error message if it's not installed
try:
    import yaml
except ImportError:
    sys.stderr.write("The Yocto Project Sphinx documentation requires PyYAML.\
    \nPlease make sure to install pyyaml python package.\n")
    sys.exit(1)

__version__  = '1.0'

# Variables substitutions. Uses {VAR} subst using variables defined in poky.yaml
# Each .rst file is processed after source-read event (subst_vars_replace runs once per file)
subst_vars = {}

def subst_vars_replace(app: Sphinx, docname, source):
    result = source[0]
    for k in subst_vars:
        result = result.replace("&"+k+";", subst_vars[k])
    source[0] = result

PATTERN = re.compile(r'&(.*?);')
def expand(val, src):
    return PATTERN.sub(lambda m: expand(src.get(m.group(1), ''), src), val)

def setup(app: Sphinx):
    #FIXME: if poky.yaml changes, files are not reprocessed.
    with open("poky.yaml") as file:
        subst_vars.update(yaml.load(file, Loader=yaml.FullLoader))

    for k in subst_vars:
        subst_vars[k] = expand(subst_vars[k], subst_vars)

    app.connect('source-read', subst_vars_replace)

    return dict(
        version = __version__,
        parallel_read_safe = True,
        parallel_write_safe = True
    )
