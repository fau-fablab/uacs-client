# uacs-client
client software for the universal access control system

This is the client for a ucas. It can communicate with the ucas-server by reading JSON Objects presented via html.

DEPRECATED: check for a hardcoded fauids permissions
DONE: change the fauid we work with from being hardcoded to being read by a cardreader
DONE: blink a led should the user have sufficient permissions
DONE: spawn a thread checking the permissions every 0.5s

TODO: Do some testing. Were ready for Alpha 0.1 :-)
TODO: Spawn thread when running oncreate. This requires the configfile system to be up and running.
TODO: configfile system
TODO: quit the spawned thread in a sane way
