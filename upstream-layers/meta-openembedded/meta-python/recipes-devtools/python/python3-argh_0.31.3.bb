SUMMARY = "An unobtrusive argparse wrapper with natural syntax"
DESCRIPTION = "Building a command-line interface? Found yourself uttering \
'argh!' while struggling with the API of argparse? Don't like the complexity \
but need the power? \
\
Everything should be made as simple as possible, but no simpler. \
\
—Albert Einstein (probably) \
\
Argh is a smart wrapper for argparse. Argparse is a very powerful \
tool; Argh just makes it easy to use."

LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=3000208d539ec061b899bce1d9ce9404 \
                    "

SRC_URI[sha256sum] = "f30023d8be14ca5ee6b1b3eeab829151d7bbda464ae07dc4dd5347919c5892f9"

inherit pypi python_flit_core

RDEPENDS:${PN} += " \
    python3-argcomplete \
    python3-logging \
"

BBCLASSEXTEND = "native nativesdk"
