import os, struct, mmap

class NotELFFileError(Exception):
    pass

class ELFFile:
    EI_NIDENT = 16

    EI_CLASS      = 4
    EI_DATA       = 5
    EI_VERSION    = 6
    EI_OSABI      = 7
    EI_ABIVERSION = 8

    E_MACHINE    = 0x12

    # possible values for EI_CLASS
    ELFCLASSNONE = 0
    ELFCLASS32   = 1
    ELFCLASS64   = 2

    # possible value for EI_VERSION
    EV_CURRENT   = 1

    # possible values for EI_DATA
    EI_DATA_NONE  = 0
    EI_DATA_LSB  = 1
    EI_DATA_MSB  = 2

    PT_INTERP = 3

    def my_assert(self, expectation, result):
        if not expectation == result:
            #print "'%x','%x' %s" % (ord(expectation), ord(result), self.name)
            raise NotELFFileError("%s is not an ELF" % self.name)

    def __init__(self, name):
        self.name = name
        self.objdump_output = {}

    # Context Manager functions to close the mmap explicitly
    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_value, traceback):
        self.data.close()

    def open(self):
        with open(self.name, "rb") as f:
            try:
                self.data = mmap.mmap(f.fileno(), 0, access=mmap.ACCESS_READ)
            except ValueError:
                # This means the file is empty
                raise NotELFFileError("%s is empty" % self.name)

        # Check the file has the minimum number of ELF table entries
        if len(self.data) < ELFFile.EI_NIDENT + 4:
            raise NotELFFileError("%s is not an ELF" % self.name)

        # ELF header
        self.my_assert(self.data[0], 0x7f)
        self.my_assert(self.data[1], ord('E'))
        self.my_assert(self.data[2], ord('L'))
        self.my_assert(self.data[3], ord('F'))
        if self.data[ELFFile.EI_CLASS] == ELFFile.ELFCLASS32:
            self.bits = 32
        elif self.data[ELFFile.EI_CLASS] == ELFFile.ELFCLASS64:
            self.bits = 64
        else:
            # Not 32-bit or 64.. lets assert
            raise NotELFFileError("ELF but not 32 or 64 bit.")
        self.my_assert(self.data[ELFFile.EI_VERSION], ELFFile.EV_CURRENT)

        self.endian = self.data[ELFFile.EI_DATA]
        if self.endian not in (ELFFile.EI_DATA_LSB, ELFFile.EI_DATA_MSB):
            raise NotELFFileError("Unexpected EI_DATA %x" % self.endian)

    def osAbi(self):
        return self.data[ELFFile.EI_OSABI]

    def abiVersion(self):
        return self.data[ELFFile.EI_ABIVERSION]

    def abiSize(self):
        return self.bits

    def isLittleEndian(self):
        return self.endian == ELFFile.EI_DATA_LSB

    def isBigEndian(self):
        return self.endian == ELFFile.EI_DATA_MSB

    def getStructEndian(self):
        return {ELFFile.EI_DATA_LSB: "<",
                ELFFile.EI_DATA_MSB: ">"}[self.endian]

    def getShort(self, offset):
        return struct.unpack_from(self.getStructEndian() + "H", self.data, offset)[0]

    def getWord(self, offset):
        return struct.unpack_from(self.getStructEndian() + "i", self.data, offset)[0]

    def isDynamic(self):
        """
        Return True if there is a .interp segment (therefore dynamically
        linked), otherwise False (statically linked).
        """
        offset = self.getWord(self.bits == 32 and 0x1C or 0x20)
        size = self.getShort(self.bits == 32 and 0x2A or 0x36)
        count = self.getShort(self.bits == 32 and 0x2C or 0x38)

        for i in range(0, count):
            p_type = self.getWord(offset + i * size)
            if p_type == ELFFile.PT_INTERP:
                return True
        return False

    def machine(self):
        """
        We know the endian stored in self.endian and we
        know the position
        """
        return self.getShort(ELFFile.E_MACHINE)

    def run_objdump(self, cmd, d):
        import bb.process
        import sys

        if cmd in self.objdump_output:
            return self.objdump_output[cmd]

        objdump = d.getVar('OBJDUMP')

        env = os.environ.copy()
        env["LC_ALL"] = "C"
        env["PATH"] = d.getVar('PATH')

        try:
            bb.note("%s %s %s" % (objdump, cmd, self.name))
            self.objdump_output[cmd] = bb.process.run([objdump, cmd, self.name], env=env, shell=False)[0]
            return self.objdump_output[cmd]
        except Exception as e:
            bb.note("%s %s %s failed: %s" % (objdump, cmd, self.name, e))
            return ""

def elf_machine_to_string(machine):
    """
    Return the name of a given ELF e_machine field or the hex value as a string
    if it isn't recognised.
    """
    try:
        return {
            0x02: "SPARC",
            0x03: "x86",
            0x08: "MIPS",
            0x14: "PowerPC",
            0x28: "ARM",
            0x2A: "SuperH",
            0x32: "IA-64",
            0x3E: "x86-64",
            0xB7: "AArch64"
        }[machine]
    except:
        return "Unknown (%s)" % repr(machine)

if __name__ == "__main__":
    import sys

    with ELFFile(sys.argv[1]) as elf:
        elf.open()
        print(elf.isDynamic())
