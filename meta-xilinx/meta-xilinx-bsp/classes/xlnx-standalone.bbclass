# Only enabled when ilp32 is enabled.
def xlnx_ilp32_dict(machdata, d):
    machdata["elf"] = {
                        "aarch64" :   (183,    0,    0,          True,          32),
                        "aarch64_be" :(183,    0,    0,          False,         32),
                      }
    return machdata

# Only enabled when microblaze64 is enabled.
def xlnx_mb64_dict(machdata, d):
    machdata["elf"] = {
                        "microblaze":  (189,   0,    0,          False,         64),
                        "microblazeeb":(189,   0,    0,          False,         64),
                        "microblazeel":(189,   0,    0,          True,          64),
                      }
    return machdata
