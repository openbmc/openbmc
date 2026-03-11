# Class to scan Python code for security issues, using Bandit.
#
# $ bitbake python-foo -c bandit
#
# Writes the report to $DEPLOY_DIR/bandit/python-foo.html.
# No output if no issues found, a warning if issues found.
#
# https://github.com/PyCQA/bandit

# Default location of sources, based on standard distutils
BANDIT_SOURCE ?= "${S}/build"

# The report format to use.
# https://bandit.readthedocs.io/en/latest/formatters/index.html
BANDIT_FORMAT ?= "html"

# Whether a scan should be done every time the recipe is built.
#
# By default the scanning needs to be done explicitly, but by setting BANDIT_AUTO
# to 1 the scan will be done whenever the recipe it built.  Note that you
# shouldn't set BANDIT_AUTO to 1 globally as it will then try to scan every
# recipe, including non-Python recipes, causing circular loops.
BANDIT_AUTO ?= "0"

# Whether Bandit finding issues results in a warning (0) or an error (1).
BANDIT_FATAL ?= "0"

do_bandit[depends] = "python3-bandit-native:do_populate_sysroot"
python do_bandit() {
    import os, subprocess
    try:
        report = d.expand("${DEPLOY_DIR}/bandit/${PN}-${PV}.${BANDIT_FORMAT}")
        os.makedirs(os.path.dirname(report), exist_ok=True)

        args = ("bandit",
                "--format", d.getVar("BANDIT_FORMAT"),
                "--output", report,
                "-ll",
                "--recursive", d.getVar("BANDIT_SOURCE"))
        subprocess.check_output(args, stderr=subprocess.STDOUT)
        bb.note("Bandit found no issues (report written to %s)" % report)
    except subprocess.CalledProcessError as e:
        if e.returncode == 1:
            if oe.types.boolean(d.getVar("BANDIT_FATAL")):
                bb.error("Bandit found issues (report written to %s)" % report)
            else:
                bb.warn("Bandit found issues (report written to %s)" % report)
        else:
            bb.error("Bandit failed:\n" + e.output.decode("utf-8"))
}

python() {
    before = "do_build"
    after = "do_compile"

    if oe.types.boolean(d.getVar("BANDIT_AUTO")):
        bb.build.addtask("do_bandit", before, after, d)
    else:
        bb.build.addtask("do_bandit", None, after, d)
}

# TODO: store report in sstate
# TODO: a way to pass extra args or .bandit file, basically control -ll
