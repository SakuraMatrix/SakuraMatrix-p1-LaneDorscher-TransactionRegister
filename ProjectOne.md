# Project 0 Requirements
- Individual project adopting enterprise standards            
- Theme: FinTech (Banking, Investing, Personal Finance, etc...)      -----------
- Org Repo name: p1-username-optionalProjectName
- Proposal document: Specify project theme, goals, user stories      -------
- Data persistence on a Cassandra node           ----------
- Dependency Injection with Spring Framework      ---------
- Reactive HTTP server with Reactor Netty        ----------
- 5-7 minute presentations on Friday, August 20th

## Technical:
- GitHub Organization repo (SakuraMatrix)
- Java 8+     ------------
  - Maven     ------------
  - Junit 4+  ------------
  - SLF4J: Log4J2, or Logback, etc. 
  - Reactor Netty (io.projectreactor.netty:reactor-netty:1.0.9) -----------
- Apache Cassandra w/ Datastax driver ------------
- Spring Framework  ----------
  - Spring Core/Beans/Context -----------
- Amazon Web Services (kinda optional)

## Quality
- Google standards
- Maven standards
- Jar packaging
- Documentation 
- Unit Testing
- Files & Console logging

## Suggested development philosophy: Suffering Oriented Programming
1. Functionality - Make it possible: build a prototype without any bells or whistles, make it as fast as you can, don't care about complex architecture (OOP), don't care about many features at first, just think about what is your MVP (minimal viable product).
2. Quality - Make it beautiful: once you have your prototype, beautify it with added features, architecture changes (OOP, packages), refactor for readability, maintainibility, extensibility
3. Performance - Make it fast: consider your bottlenecks and future scalability issues, and improve what is economical at that moment.

## Suggested development methodology: Agile
- Documented user stories
- Standup: GitHub post w/ daily work accomplished, current blockers, & future work planned
