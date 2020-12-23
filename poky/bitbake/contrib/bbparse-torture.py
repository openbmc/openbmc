#! /usr/bin/env python3
#
# Copyright (C) 2020 Joshua Watt <JPEWhacker@gmail.com>
#
# SPDX-License-Identifier: MIT

import argparse
import os
import random
import shutil
import signal
import subprocess
import sys
import time


def try_unlink(path):
    try:
        os.unlink(path)
    except:
        pass


def main():
    def cleanup():
        shutil.rmtree("tmp/cache", ignore_errors=True)
        try_unlink("bitbake-cookerdaemon.log")
        try_unlink("bitbake.sock")
        try_unlink("bitbake.lock")

    parser = argparse.ArgumentParser(
        description="Bitbake parser torture test",
        epilog="""
        A torture test for bitbake's parser. Repeatedly interrupts parsing until
        bitbake decides to deadlock.
        """,
    )

    args = parser.parse_args()

    if not "BUILDDIR" in os.environ:
        print(
            "'BUILDDIR' not found in the environment. Did you initialize the build environment?"
        )
        return 1

    os.chdir(os.environ["BUILDDIR"])

    run_num = 0
    while True:
        if run_num % 100 == 0:
            print("Calibrating wait time...")
            cleanup()

            start_time = time.monotonic()
            r = subprocess.run(["bitbake", "-p"])
            max_wait_time = time.monotonic() - start_time

            if r.returncode != 0:
                print("Calibration run exited with %d" % r.returncode)
                return 1

            print("Maximum wait time is %f seconds" % max_wait_time)

        run_num += 1
        wait_time = random.random() * max_wait_time

        print("Run #%d" % run_num)
        print("Will sleep for %f seconds" % wait_time)

        cleanup()
        with subprocess.Popen(["bitbake", "-p"]) as proc:
            time.sleep(wait_time)
            proc.send_signal(signal.SIGINT)
            try:
                proc.wait(45)
            except subprocess.TimeoutExpired:
                print("Run #%d: Waited too long. Possible deadlock!" % run_num)
                proc.wait()
                return 1

            if proc.returncode == 0:
                print("Exited successfully. Timeout too long?")
            else:
                print("Exited with %d" % proc.returncode)


if __name__ == "__main__":
    sys.exit(main())
