SUMMARY = "Perfetto - System profiling, app tracing and trace analysis."
HOMEPAGE = "https://github.com/google/perfetto"

# Add only patches here which are used by both perfetto and libperfetto
SRC_URI = "git://github.com/google/perfetto.git;protocol=https;name=perfetto;nobranch=1 \
	   file://0001-fix-musl-build.patch \
	"

SRCREV_perfetto = "c74251226a8caa0b43377902ee06d2570faa0c15"
SRCREV_FORMAT = "perfetto"
PV = "47.0"

S = "${WORKDIR}/git"
