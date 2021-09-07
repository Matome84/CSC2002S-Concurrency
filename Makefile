default: 
	javac -g ./src/*.java 
	mv ./src/*.class ./bin

clean:
		$(RM) ./bin/*.class
#		$(RM) ./bin/*.txt

docs:
	javadoc -d ./doc ./src/*

archive:
	git archive master --format=zip --output=mccrob015_csc2002s_assignmentX.zip

setup:
	mkdir bin
	mkdir doc
	mkdir src
	git init
	touch bin/.gitignore
	git add bin/.gitignore Makefile
	git commit -m "Setup"

