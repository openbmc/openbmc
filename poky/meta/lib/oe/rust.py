# Handle mismatches between `uname -m`-style output and Rust's arch names
def arch_to_rust_arch(arch):
    if arch == "ppc64le":
        return "powerpc64le"
    return arch
