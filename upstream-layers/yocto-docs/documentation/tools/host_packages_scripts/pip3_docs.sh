python3 -m venv --clear ./yocto-docs-venv
. ./yocto-docs-venv/bin/activate
python3 -m pip install sphinx sphinx_rtd_theme pyyaml sphinx-copybutton 'sphinxcontrib-svg2pdfconverter>=2.0.0'
