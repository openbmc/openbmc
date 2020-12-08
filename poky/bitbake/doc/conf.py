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
# import os
# import sys
# sys.path.insert(0, os.path.abspath('.'))

import datetime

current_version = "dev"

# String used in sidebar
version = 'Version: ' + current_version
if current_version == 'dev':
    version = 'Version: Current Development'
# Version seen in documentation_options.js and hence in js switchers code
release = current_version

# -- Project information -----------------------------------------------------

project = 'Bitbake'
copyright = '2004-%s, Richard Purdie, Chris Larson, and Phil Blundell' \
    % datetime.datetime.now().year
author = 'Richard Purdie, Chris Larson, and Phil Blundell'

# external links and substitutions
extlinks = {
    'yocto_docs': ('https://docs.yoctoproject.org%s', None),
    'oe_lists': ('https://lists.openembedded.org%s', None),
}

# -- General configuration ---------------------------------------------------

# Add any Sphinx extension module names here, as strings. They can be
# extensions coming with Sphinx (named 'sphinx.ext.*') or your custom
# ones.
extensions = [
    'sphinx.ext.autosectionlabel',
    'sphinx.ext.extlinks',
]
autosectionlabel_prefix_document = True

# Add any paths that contain templates here, relative to this directory.
templates_path = ['_templates']

# List of patterns, relative to source directory, that match files and
# directories to ignore when looking for source files.
# This pattern also affects html_static_path and html_extra_path.
exclude_patterns = ['_build', 'Thumbs.db', '.DS_Store']

# master document name. The default changed from contents to index. so better
# set it ourselves.
master_doc = 'index'

# create substitution for project configuration variables
rst_prolog = """
.. |project_name| replace:: %s
.. |copyright| replace:: %s
.. |author| replace:: %s
""" % (project, copyright, author)

# -- Options for HTML output -------------------------------------------------

# The theme to use for HTML and HTML Help pages.  See the documentation for
# a list of builtin themes.
#
try:
    import sphinx_rtd_theme
    html_theme = 'sphinx_rtd_theme'
except ImportError:
    sys.stderr.write("The Sphinx sphinx_rtd_theme HTML theme was not found.\
    \nPlease make sure to install the sphinx_rtd_theme python package.\n")
    sys.exit(1)

# Add any paths that contain custom static files (such as style sheets) here,
# relative to this directory. They are copied after the builtin static files,
# so a file named "default.css" will overwrite the builtin "default.css".
html_static_path = ['sphinx-static']

# Add customm CSS and JS files
html_css_files = ['theme_overrides.css']
html_js_files = ['switchers.js']

# Hide 'Created using Sphinx' text
html_show_sphinx = False

# Add 'Last updated' on each page
html_last_updated_fmt = '%b %d, %Y'

# Remove the trailing 'dot' in section numbers
html_secnumber_suffix = " "
