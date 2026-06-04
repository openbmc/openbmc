# SPDX-FileCopyrightText: <text>Copyright 2026 Arm Limited and/or its
# affiliates <open-source-office@arm.com></text>
#
# SPDX-License-Identifier: MIT

# Configuration file for the Sphinx documentation builder.
#
# This file only contains a selection of the most common options. For a full
# list see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

# -- Path setup --------------------------------------------------------------

# If extensions (or modules to document with autodoc) are in another directory,
# add these directories to sys.path here. If the directory is relative to the
# documentation root, use os.path.abspath to make it absolute, like shown here.
#
# sys.path.insert(0, os.path.abspath('.'))

import os
import re
import sys

import yaml

# Append the documentation directory to the path, so we can import variables
sys.path.append(os.path.dirname(__file__))

_metadata_path = os.path.join(os.path.dirname(__file__), 'corstone-a320_metadata.yaml')
with open(_metadata_path, encoding='utf-8') as metadata_file:
    _metadata = yaml.safe_load(metadata_file) or {}

_metadata_variables = {
    item['name']: item['value']
    for item in _metadata.get('variables', [])
    if item.get('name') and item.get('value')
}


# -- Project information -----------------------------------------------------

project = 'Corstone-1000 Armv9-A Edge-AI'
copyright = '2026, Arm Limited'
author = 'Arm Limited'


# -- General configuration ---------------------------------------------------

# Add any Sphinx extension module names here, as strings. They can be
# extensions coming with Sphinx (named 'sphinx.ext.*') or your custom
# ones.
extensions = [
    'myst_parser',
    'sphinx_rtd_theme',
]

source_suffix = {
    '.rst': 'restructuredtext',
    '.md': 'markdown',
}

myst_enable_extensions = [
    'colon_fence',
]

# Add any paths that contain templates here, relative to this directory.
templates_path = ['_templates']

# List of patterns, relative to source directory, that match files and
# directories to ignore when looking for source files.
# This pattern also affects html_static_path and html_extra_path.
exclude_patterns = [
    '_build',
    'Thumbs.db',
    '.DS_Store',
    'docs/infra',
    'corstone-a320_map.md',
    'corstone-a320_metadata.yaml',
]


# -- Options for HTML output -------------------------------------------------

# The theme to use for HTML and HTML Help pages.  See the documentation for
# a list of builtin themes.
#
html_theme = 'sphinx_rtd_theme'
html_theme_options = {
    'flyout_display': 'attached',
}

# Define the canonical URL if you are using a custom domain on Read the Docs
html_baseurl = os.environ.get("READTHEDOCS_CANONICAL_URL", "")

# Tell Jinja2 templates the build is running on Read the Docs
if os.environ.get("READTHEDOCS", "") == "True":
    if "html_context" not in globals():
        html_context = {}
    html_context["READTHEDOCS"] = True


# Add any paths that contain custom static files (such as style sheets) here,
# relative to this directory. They are copied after the builtin static files,
# so a file named "default.css" will overwrite the builtin "default.css".
#html_static_path = ['_static']


def _replace_metadata_variables(app, docname, source):
    source[0] = re.sub(
        r'\$([A-Za-z0-9_]+)',
        lambda match: _metadata_variables.get(match.group(1), match.group(0)),
        source[0],
    )


def setup(app):
    app.connect('source-read', _replace_metadata_variables)
