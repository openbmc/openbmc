SUMMARY = "Nushell is a modern shell for structured data"
HOMEPAGE = "https://crates.io/crates/nu"
DESCRIPTION = "Nushell is a modern shell that works with structured data rather than text. \
               Nushell takes cues from a lot of familiar territory: traditional shells like \
               bash, object based shells like PowerShell, gradually typed languages like \
               TypeScript, functional programming, systems programming, and more."

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=ea22f3cfd911b3519505220cfc0ce542 \
"

SRC_URI = "crate://crates.io/nu/${PV};name=nu"
SRC_URI[nu.sha256sum] = "a840c40450e4ab0b95c81aa3ef965f31a5da47f6e6823f00da5edfe9fcdf05c9"

S = "${CARGO_VENDORING_DIRECTORY}/nu-${PV}"

inherit cargo cargo-update-recipe-crates

require ${BPN}-crates.inc

BBCLASSEXTEND = "native"
