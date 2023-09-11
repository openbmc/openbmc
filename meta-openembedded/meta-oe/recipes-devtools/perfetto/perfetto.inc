SUMMARY = "Perfetto - System profiling, app tracing and trace analysis."
HOMEPAGE = "https://github.com/google/perfetto"

SRC_URI = "git://github.com/google/perfetto.git;protocol=https;name=perfetto;nobranch=1"

SRCREV_perfetto = "b8da07095979310818f0efde2ef3c69ea70d62c5"
SRCREV_FORMAT = "perfetto"
PV = "31.0"

S = "${WORKDIR}/git"
