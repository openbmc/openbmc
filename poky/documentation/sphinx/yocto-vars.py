#!/usr/bin/env python
from hashlib import md5
from pathlib import Path
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

poky_hash = ""

def subst_vars_replace(app: Sphinx, docname, source):
    result = source[0]
    for k in subst_vars:
        result = result.replace("&"+k+";", subst_vars[k])
    source[0] = result

def yocto_vars_env_get_outdated(app: Sphinx, env, added, changed, removed):
    '''
    If poky.yaml changed (BUILDDIR/.poky.yaml.cache does not exist or contains
    an md5sum different from poky.yaml's current md5sum), force rebuild of all
    *.rst files in SOURCEDIR whose content has at least one occurence of `&.*;`
    (see PATTERN global variable).
    '''
    try:
        poky_cache = Path(app.outdir) / ".poky.yaml.cache"
        cache_hash = poky_cache.read_text()
    except FileNotFoundError:
        cache_hash = None

    if poky_hash == cache_hash:
        return []

    docs = []
    for p in Path(app.srcdir).rglob("*.rst"):
        if PATTERN.search(p.read_text()):
            p_rel_no_ext = p.relative_to(app.srcdir).parent / p.stem
            docs.append(str(p_rel_no_ext))
    return docs

def yocto_vars_build_finished(app: Sphinx, exception):
    poky_cache = Path(app.outdir) / ".poky.yaml.cache"
    poky_cache.write_text(poky_hash)
    return []

PATTERN = re.compile(r'&(.*?);')
def expand(val, src):
    return PATTERN.sub(lambda m: expand(src.get(m.group(1), ''), src), val)

def setup(app: Sphinx):
    global poky_hash

    with open("poky.yaml") as file:
        hasher = md5()
        buff = file.read()
        hasher.update(buff.encode('utf-8'))
        poky_hash = hasher.hexdigest()
        subst_vars.update(yaml.safe_load(buff))

    for k in subst_vars:
        subst_vars[k] = expand(subst_vars[k], subst_vars)

    app.connect('source-read', subst_vars_replace)
    app.connect('env-get-outdated', yocto_vars_env_get_outdated)
    app.connect('build-finished', yocto_vars_build_finished)

    return dict(
        version = __version__,
        parallel_read_safe = True,
        parallel_write_safe = True
    )
