inherit cargo ptest

RUST_TEST_ARGS ??= ""
RUST_TEST_ARGS[doc] = "Arguments to give to the test binaries (e.g. --shuffle)"

# I didn't find a cleaner way to share data between compile and install tasks
CARGO_TEST_BINARIES_FILES ?= "${B}/test_binaries_list"

# Sadly, generated test binaries have no deterministic names (https://github.com/rust-lang/cargo/issues/1924)
# This forces us to parse the cargo output in json format to find those test binaries.
python do_compile_ptest_cargo() {
    import subprocess
    import json

    cargo = bb.utils.which(d.getVar("PATH"), d.getVar("CARGO", True))
    cargo_build_flags = d.getVar("CARGO_BUILD_FLAGS", True)
    rust_flags = d.getVar("RUSTFLAGS", True)
    manifest_path = d.getVar("CARGO_MANIFEST_PATH", True)
    project_manifest_path = os.path.normpath(manifest_path)
    manifest_dir = os.path.dirname(manifest_path)

    env = os.environ.copy()
    env['RUSTFLAGS'] = rust_flags
    cmd = f"{cargo} build --tests --message-format json {cargo_build_flags}"
    bb.note(f"Building tests with cargo ({cmd})")

    try:
        proc = subprocess.Popen(cmd, shell=True, env=env, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    except subprocess.CalledProcessError as e:
        bb.fatal(f"Cannot build test with cargo: {e}")

    lines = []
    for line in proc.stdout:
        data = line.strip('\n')
        lines.append(data)
        bb.note(data)
    proc.communicate()
    if proc.returncode != 0:
        bb.fatal(f"Unable to compile test with cargo, '{cmd}' failed")

    # Definition of the format: https://doc.rust-lang.org/cargo/reference/external-tools.html#json-messages
    test_bins = []
    for line in lines:
        try:
            data = json.loads(line)
        except json.JSONDecodeError:
            # skip lines that are not a json
            pass
        else:
            try:
                # Filter the test packages coming from the current project:
                #    - test binaries from the root manifest
                #    - test binaries from sub manifest of the current project if any
                current_manifest_path = os.path.normpath(data['manifest_path'])
                common_path = os.path.commonpath([current_manifest_path, project_manifest_path])
                if common_path in [manifest_dir, current_manifest_path]:
                    if (data['target']['test'] or data['target']['doctest']) and data['executable']:
                        test_bins.append(data['executable'])
            except (KeyError, ValueError) as e:
                # skip lines that do not meet the requirements
                pass

    # All rust project will generate at least one unit test binary
    # It will just run a test suite with 0 tests, if the project didn't define some
    # So it is not expected to have an empty list here
    if not test_bins:
        bb.fatal("Unable to find any test binaries")

    cargo_test_binaries_file = d.getVar('CARGO_TEST_BINARIES_FILES', True)
    bb.note(f"Found {len(test_bins)} tests, write their paths into {cargo_test_binaries_file}")
    with open(cargo_test_binaries_file, "w") as f:
        for test_bin in test_bins:
            f.write(f"{test_bin}\n")

}

python do_install_ptest_cargo() {
    import shutil

    dest_dir = d.getVar("D", True)
    pn = d.getVar("PN", True)
    ptest_path = d.getVar("PTEST_PATH", True)
    cargo_test_binaries_file = d.getVar('CARGO_TEST_BINARIES_FILES', True)
    rust_test_args = d.getVar('RUST_TEST_ARGS') or ""

    ptest_dir = os.path.join(dest_dir, ptest_path.lstrip('/'))
    os.makedirs(ptest_dir, exist_ok=True)

    test_bins = []
    with open(cargo_test_binaries_file, "r") as f:
        for line in f.readlines():
            test_bins.append(line.strip('\n'))

    test_paths = []
    for test_bin in test_bins:
        shutil.copy2(test_bin, ptest_dir)
        test_paths.append(os.path.join(ptest_path, os.path.basename(test_bin)))

    ptest_script = os.path.join(ptest_dir, "run-ptest")
    if os.path.exists(ptest_script):
        with open(ptest_script, "a") as f:
            f.write(f"\necho \"\"\n")
            f.write(f"echo \"## starting to run rust tests ##\"\n")
            for test_path in test_paths:
                f.write(f"{test_path} {rust_test_args}\n")
    else:
        with open(ptest_script, "a") as f:
            f.write("#!/bin/sh\n")
            for test_path in test_paths:
                f.write(f"{test_path} {rust_test_args}\n")
        os.chmod(ptest_script, 0o755)

    # this is chown -R root:root ${D}${PTEST_PATH}
    for root, dirs, files in os.walk(ptest_dir):
        for d in dirs:
            shutil.chown(os.path.join(root, d), "root", "root")
        for f in files:
            shutil.chown(os.path.join(root, f), "root", "root")
}

do_install_ptest_cargo[dirs] = "${B}"
do_install_ptest_cargo[doc] = "Create or update the run-ptest script with rust test binaries generated"
do_compile_ptest_cargo[dirs] = "${B}"
do_compile_ptest_cargo[doc] = "Generate rust test binaries through cargo"

addtask compile_ptest_cargo after do_compile            before do_compile_ptest_base
addtask install_ptest_cargo after do_install_ptest_base before do_package

python () {
    if not bb.data.inherits_class('native', d) and not bb.data.inherits_class('cross', d):
        d.setVarFlag('do_install_ptest_cargo', 'fakeroot', '1')
        d.setVarFlag('do_install_ptest_cargo', 'umask', '022')

    # Remove all '*ptest_cargo' tasks when ptest is not enabled
    if not(d.getVar('PTEST_ENABLED') == "1"):
        for i in ['do_compile_ptest_cargo', 'do_install_ptest_cargo']:
            bb.build.deltask(i, d)
}
