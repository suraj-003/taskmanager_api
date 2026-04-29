# DECISION LOG

## 1. Time Breakdown
- Project setup and folder structure: 20 minutes
- Core Java files and basic endpoints: 60 minutes
- AI priority feature integration: 70 minutes
- Testing all endpoints: 40 minutes
- Bug fixes and improvements: 35 minutes
- Documentation: 30 minutes
- Total: approximately 5.30 hours

## 2. Where AI Was Used and Why

### Boilerplate Code
Used AI to generate initial HttpServer setup in Main.java
and basic route structure in TaskHandler.java.
Reason: HttpServer setup is repetitive ceremony that
consumes time without adding thinking value.
AI gave working skeleton in 2 minutes saving 40 minutes.

### Prompt Engineering
Used AI to help design the prompt format in AIService.java.
Reason: Prompt design is iterative. AI helped test different
phrasings quickly. Final format decision was mine because
I needed structured output that my parser could extract.

### README Structure
Used AI to generate first draft of README.md.
Reason: Documentation structure is standard and repetitive.
AI gave 80% complete README in 1 minute which I then edited
to match my actual code and corrected wrong assumptions.

### Debugging
Used AI to identify missing response close() method in
TaskHandler.java that was causing empty PATCH responses.
Reason: Faster than reading Java HTTP documentation.
I verified the fix before applying it.

## 3. Where AI Was NOT Used and Why

### Architecture Decisions
I decided the folder structure and class design myself.
Reason: Architecture requires understanding the full
assignment constraints which AI cannot fully grasp.

### Validation Logic
I wrote the title required check myself.
Reason: Business rules should come from developer
understanding of requirements not AI assumptions.

### Error Handling Strategy
I decided the graceful degradation approach myself.
Reason: Deciding what happens when AI fails requires
judgment about user experience not just code generation.

### Security Fix
When GitHub blocked push due to exposed API key I
identified and fixed the issue myself by moving key
to environment variable.
Reason: Security decisions require careful human judgment.

### Trade-off Decisions
All scoping decisions were made by me.
Reason: Prioritization requires understanding constraints
that only I know - time available, my skill level, spec requirements.

## 4. At Least 2 Bad AI Outputs

### Bad Output #1 - JSON Parser Breaks on Special Characters
AI generated extractJson method using simple indexOf to find
closing quote. This breaks when task title contains special
characters like escaped quotes inside values.
Example: title "Fix login bug" with quotes inside would
be parsed incorrectly - returning wrong substring.

How I identified it:
I reviewed the parsing logic carefully and realized it
assumes the first quote after value start is always the
closing quote. This is wrong for escaped characters.

What I changed:
Replaced simple indexOf with a while loop that checks
if each quote character is escaped before treating it
as the closing quote of the value.

### Bad Output #2 - AI Response Parser Crashes on Error Response
AI generated code that directly calls indexOf on OpenAI
response assuming content field always exists in response.
But when API key is invalid or service is down OpenAI
returns error JSON with no content field causing
StringIndexOutOfBoundsException crash.

How I identified it:
I tested deliberately with wrong API key to see what
happens in error scenario. Server threw exception
instead of returning graceful fallback response.

What I changed:
Added defensive check before parsing - if content field
is missing in response return safe default values
instead of crashing the entire task creation request.

### Bonus - AI Never Warned About API Key Security
AI suggested hardcoding API key directly in source code.
It never mentioned this would be blocked by GitHub secret
scanning or that it is a security risk.

How I identified it:
GitHub blocked my push with security violation error
clearly stating OpenAI API key was detected in commits.

What I changed:
Moved API key to environment variable using
System.getenv("OPENAI_API_KEY") which is the correct
professional approach for handling secrets.

## 5. Trade-offs Made

### No Authentication
Decision: Skipped auth completely.
Reason: Assignment spec did not mention authentication.
Adding it would cost 60-90 minutes with no spec requirement.

### No Proper JSON Library
Decision: Manual JSON parsing instead of Gson or Jackson.
Reason: Assignment said keep dependencies minimal.
Manual parsing works for the simple data structures in spec.
Known limitation: breaks on deeply nested or complex JSON.

### In-Memory Storage Only
Decision: HashMap instead of SQLite or real database.
Reason: Assignment explicitly said in-memory is acceptable.
Saves 45-60 minutes of database setup and configuration.

### No Pagination
Decision: GET /tasks returns all tasks always.
Reason: Out of scope for assignment. With more time
would add limit and offset query parameters.

### No Unit Tests
Decision: Manual curl testing instead of JUnit tests.
Reason: Time constraint. Tests would take 60-90 minutes
to write properly. Manual testing verified all endpoints work.

### Single AI Feature
Decision: Implemented only auto-priority suggestion.
Reason: Assignment explicitly said pick one and do it well.
Chose priority because output is structured and testable.

## 6. What I Would Improve With More Time

### First Priority - Proper JSON Library
Add Gson or Jackson for reliable JSON parsing.
Current manual parser has known limitations with
special characters that a library handles automatically.

### Second Priority - Environment Configuration
Create proper config file for API key and port number
instead of hardcoded values and environment variables.

### Third Priority - Unit Tests
Add JUnit tests for all endpoints and edge cases.
Currently only manually tested with curl commands.

### Fourth Priority - Input Sanitization
Add proper validation for all input fields.
Currently only title required check exists.

### Fifth Priority - Proper Logging
Replace System.out.println with proper logging framework
like java.util.logging for production-ready output.