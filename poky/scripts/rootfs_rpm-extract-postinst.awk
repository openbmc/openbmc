/Name:.*/ {
  package = substr($0, 7)
  next
}
/postinstall.*scriptlet .*/ {
  next
}
{
  print $0 >> ENVIRON["D"] "/etc/rpm-postinsts/" package ".sh"
}
    
