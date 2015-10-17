#CATALINA_OPTS="-XX:MaxPermSize=128m -Xmx512m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 -Dfile.encoding=UTF-8"
CATALINA_OPTS="-XX:MaxPermSize=128m -Xmx512m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006 -Dfile.encoding=UTF-8"
#-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
