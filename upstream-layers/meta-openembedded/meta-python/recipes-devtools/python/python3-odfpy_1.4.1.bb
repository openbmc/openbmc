SUMMARY = "Python API and tools to manipulate OpenDocument files"
DESCRIPTION = "Odfpy is a library to read and write OpenDocument v. 1.2 \
files. The main focus has been to prevent the programmer from creating \
invalid documents. It has checks that raise an exception if the programmer \
adds an invalid element, adds an attribute unknown to the grammar, forgets \
to add a required attribute or adds text to an element that doesn’t allow it. \
\
These checks and the API itself were generated from the RelaxNG schema, and \
then hand-edited. Therefore the API is complete and can handle all ODF \
constructions. \
\
In addition to the API, there are a few scripts: \
    csv2odf - Create OpenDocument spreadsheet from comma separated values \
    mailodf - Email ODF file as HTML archive \
    odf2xhtml - Convert ODF to (X)HTML \
    odf2mht - Convert ODF to HTML archive \
    odf2xml - Create OpenDocument XML file from OD? package \
    odfimgimport - Import external images \
    odflint - Check ODF file for problems \
    odfmeta - List or change the metadata of an ODF file \
    odfoutline - Show outline of OpenDocument \
    odfuserfield - List or change the user-field declarations in an ODF file \
    xml2odf - Create OD? package from OpenDocument in XML form \
\
The source code is at https://github.com/eea/odfpy \
\
Visit https://github.com/eea/odfpy/wiki for documentation and examples. \
\
The code at https://joinup.ec.europa.eu/software/odfpy/home is obsolete."
LICENSE = "Apache-2.0 | (GPL-2.0-or-later & LGPL-2.0-or-later)"
LIC_FILES_CHKSUM = " \
	file://APACHE-LICENSE-2.0.txt;md5=3b83ef96387f14655fc854ddc3c6bd57 \
	file://GPL-LICENSE-2.txt;md5=751419260aa954499f7abaabaa882bbe \
"

PYPI_PACKAGE = "odfpy"

inherit pypi ptest-python-pytest setuptools3
SRC_URI[sha256sum] = "db766a6e59c5103212f3cc92ec8dd50a0f3a02790233ed0b52148b70d3c438ec"

SRC_URI += "file://run-ptest"

RDEPENDS:${PN} = "python3-defusedxml"
