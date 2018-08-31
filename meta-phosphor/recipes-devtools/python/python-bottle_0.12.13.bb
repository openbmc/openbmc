SRC_URI[sha256sum] = "39b751aee0b167be8dffb63ca81b735bbf1dd0905b3bc42761efedee8f123355"
SRC_URI[md5sum] = "d2fe1b48c1d49217e78bf326b1cad437"

# There is no license on pypi.  See:
# https://github.com/bottlepy/bottle/commit/55a505b3a54bb7de23e9554cb8ce7f8e160c31a0
SRC_URI += "file://LICENSE"

SRC_URI += "file://json-format.patch"

require python-bottle.inc
