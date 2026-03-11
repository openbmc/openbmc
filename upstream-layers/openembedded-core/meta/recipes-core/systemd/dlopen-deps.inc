PACKAGEFUNCS =+ "package_generate_dlopen_deps"

python package_generate_dlopen_deps() {
    # https://systemd.io/ELF_DLOPEN_METADATA/

    import struct, json

    def extract_segment(filename, segment):
        """
        Return the named segment from the ELF.
        """
        import tempfile, subprocess

        with tempfile.NamedTemporaryFile() as f:
            try:
                cmd = [d.getVar("OBJCOPY"), "--dump-section", f"{segment}={f.name}", filename]
                subprocess.run(cmd, check=True)
                with open(f.name, "rb") as f2:
                    return f2.read()
            except subprocess.CalledProcessError as e:
                # binutils-objcopy has 0 exit code if the segment can't be found, but llvm-objcopy
                # does not. Assume the failure isn't critical and ignore errors.
                if e.returncode == 1:
                    return b""
                raise e

    def parse(buffer, is_little):
        deps = []
        offset = 0
        while offset < len(buffer):
            format = f"{'<' if is_little else '>'}iii"
            name_size, desc_size, note_type = struct.unpack_from(format, buffer, offset)
            offset += struct.calcsize(format)

            format = f"{name_size}s0i{desc_size}s0i"
            if note_type == 0x407c0c0a:
                name_b, desc_b = struct.unpack_from(format, buffer, offset)
                name = name_b.strip(b"\x00").decode("ascii")
                if name == "FDO":
                    desc = desc_b.strip(b"\x00").decode("utf-8")
                    deps.append(*json.loads(desc))
            offset += struct.calcsize(format)
        return deps

    dep_map = {
        "required": "RDEPENDS",
        "recommended": "RRECOMMENDS",
        "suggested": "RSUGGESTS"
    }

    shlibs = oe.package.read_shlib_providers(d)

    for pkg, files in pkgfiles.items():
        # Skip -dbg packages as we won't need to generate dependencies for those
        # but scanning can take time
        if pkg.endswith("-dbg"):
            continue

        for f in files:
            # Skip symlinks, just look for real libraries
            if cpath.islink(f):
                continue

            if ".so." in f or f.endswith(".so"):
                try:
                    elf = oe.qa.ELFFile(f)
                    elf.open()
                    for dep in parse(extract_segment(f, ".note.dlopen"), elf.isLittleEndian()):
                        for soname in dep["soname"]:
                            if soname in shlibs:
                                # TODO assumes the first match is good
                                package, version = list(shlibs[soname].values())[0]
                                dependency = dep_map[dep["priority"]]
                                bb.note(f"{pkg}: adding {dependency} on {package} via .note.dlopen")
                                d.appendVar(f"{dependency}:{pkg}", f" {package} (>= {version})")
                            else:
                                bb.warn(f"cannot find {soname}")
                except oe.qa.NotELFFileError as e:
                    bb.note(f"Cannot extract ELF notes: {e}")
                    pass
}
