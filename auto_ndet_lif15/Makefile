CC=g++
#CFLAGS=-c -Wall -Wextra -ansi -pedantic -std=c++11 -stdlib=libc++
CFLAGS=-c -Wall -Wextra
LDFLAGS=
SOURCES=src/ndet.cpp src/expression_rationnelle.cpp src/parser.tab.cpp src/lex.yy.cpp
OBJECTS=$(SOURCES:.cpp=.o)
EXECUTABLE=ndet

all: $(SOURCES) $(EXECUTABLE)
	
$(EXECUTABLE): $(OBJECTS) 
	$(CC) $(LDFLAGS) $(OBJECTS) -o $@
	
	
.cpp.o:
	$(CC) $(CFLAGS) $< -o $@

src/parser.tab.cpp: src/parser.y
	bison -d src/parser.y --output-file=src/parser.tab.cpp

src/lex.yy.cpp: src/parser.lex
	flex -o src/lex.yy.cpp src/parser.lex 


	
clean:
	rm -f ndet src/*.o src/*~ *~ 
cleanparser:
	rm -f src/lex.yy.cpp src/parser.tab.cpp
  # $(EXECUTABLE)

keyboardcat:
	firefox http://www.youtube.com/watch?v=J---aiyznGQ &
