#!/usr/bin/env python3

#Signed-off-by: Anne Mulhern <mulhern@yoctoproject.org>

import argparse, os, shutil, sys, tempfile, traceback
from os import path



def get_config(lines):
  """
  From a sequence of lines retrieve the question file name, question identifier
  pairs.
  """
  for l in lines:
    if not l.startswith("#"):
      try:
        (coord, value) = l.split("=")
        try:
          (fname, ident) = coord.split(".")
          yield fname, ident
        except ValueError as e:
          raise ValueError("Badly formatted coordinates %s in line %s." % (coord, l.strip()))
      except ValueError as e:
        raise ValueError("Skipping badly formatted line %s, %s" % (l.strip(), e))



def check_contains(line, name):
  """
  Check if the value field for REQUIRE_DISTRO contains the given name.
  @param name line The REQUIRE_DISTRO line
  @param name name The name to look for in the value field of the line.
  """
  try:
    (label, distros) = line.split(":")
    return name in distros.split()
  except ValueError as e:
    raise ValueError("Error splitting REQUIRE_DISTRO line: %s" % e)



def add_requires(the_ident, distro, lines):

  """
  Yield a sequence of lines the same as lines except that where
  the_ident matches a question identifier change the REQUIRE_DISTRO so that
  it includes the specified distro.

  @param name the_ident The question identifier to be matched.
  @param name distro The distribution to added to the questions REQUIRE_DISTRO
                     field.
  @param lines The sequence to be processed.
  """
  for l in lines:
    yield l
    if l.startswith("LABEL:"):
      try:
        (label, ident) = l.split(":")
        if ident.strip() == the_ident:
          break
      except ValueError as e:
        raise ValueError("Unexpected line %s in questions file." % l.strip())
  for l in lines:
    if l.startswith("REQUIRE_DISTRO"):
      if not check_contains(l, distro):
        yield l.rstrip() + " " + distro + "\n"
      else:
        yield l
      break;
    else:
      yield l
  for l in lines:
    yield l



def xform_file(qfile, distro, qlabel):
  """
  Transform a Questions file.
  @param name qfile The designated questions file.
  @param name distro The distribution to add to the required distributions.
  @param name qlabel The question label for which the distro is to be added.
  """
  questions_in = open(qfile)
  questions_out = tempfile.NamedTemporaryFile(mode="w+", delete=False)
  for l in add_requires(qlabel, distro, questions_in):
    questions_out.write(l)
  questions_out.close()
  questions_in.close()
  shutil.copystat(qfile, questions_out.name)
  os.remove(qfile)
  shutil.move(questions_out.name, qfile)



def handle_args(parser):
  parser.add_argument('config_file',
                      help = "Configuration file path.")
  parser.add_argument('questions_dir',
                      help = "Directory containing Questions files.")
  parser.add_argument('--distro', '-d',
                      help = "The distribution, the default is Yocto.",
                      default = "Yocto")
  parser.add_argument('--debug', '-b',
                      help = "Print debug information.",
                      action = 'store_true')
  return parser.parse_args()



def check_args(args):
  args.config_file = os.path.abspath(args.config_file)
  args.questions_dir = os.path.abspath(args.questions_dir)

  if not os.path.isdir(args.questions_dir):
    raise ValueError("Specified Questions directory %s does not exist or is not a directory." % args.questions_dir)

  if not os.path.isfile(args.config_file):
    raise ValueError("Specified configuration file %s not found." % args.config_file)



def main():
  opts = handle_args(argparse.ArgumentParser(description="A simple script that sets required questions based on the question/answer pairs in a configuration file."))

  try:
    check_args(opts)
  except ValueError as e:
    if opts.debug:
      traceback.print_exc()
    else:
      sys.exit("Fatal error:\n%s" % e)


  try:
    config_in = open(opts.config_file)
    for qfile, qlabel in get_config(config_in):
      questions_file = os.path.join(opts.questions_dir, qfile + ".txt")
      xform_file(questions_file, opts.distro, qlabel)
    config_in.close()

  except IOError as e:
    if opts.debug:
      traceback.print_exc()
    else:
      sys.exit("Fatal error reading or writing file:\n%s" % e)
  except ValueError as e:
    if opts.debug:
      traceback.print_exc()
    else:
      sys.exit("Fatal error:\n%s" % e)



if __name__ == "__main__":
  main()
