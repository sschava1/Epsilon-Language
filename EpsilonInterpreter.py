import sys

# Capture the information of the Definition
class DefinitionDescription:
    def __init__(self):
        self.def_name = ""
        self.def_start_location = 0
        self.number_of_parameters = 1
        self.symbol_values = {}
        self.local_stack = []

# Runtime Environment
class RuntimeEnvironment:

    def __init__(self):
        self.definition_description = []
        self.call_stack = []
        self.param_list = []
        self.return_list = []

# Epsilon Interpreter
class EpsilonInterpreter:

	# Initialize the interpreter and scan the program
    def __init__(self, intermediate_code=[]):
        self.runtime =  RuntimeEnvironment()
        self.intermediate_code = intermediate_code
        self.instruction_pointer = 0
        self.total_lines_of_code = len(self.intermediate_code)
		# Capture the information of the definition
        while self.instruction_pointer < self.total_lines_of_code:
            statement = self.intermediate_code[self.instruction_pointer]
            statement = statement.split()
            if len(statement) > 0:
                opcode = statement[0]
               
                if opcode == "DEFN":
                    def_description = DefinitionDescription()
                    def_description.def_name = statement[1]
                    def_description.def_start_location = self.instruction_pointer
                    self.instruction_pointer += 1
                    statement = self.intermediate_code[self.instruction_pointer]
                    statement = statement.split()
                    opcode = statement[0]
                    parameter_count = 0
                    while opcode == "SAVE":
                        parameter_count += 1
                        def_parameter = statement[1]
                        def_description.symbol_values[def_parameter] = None
                        self.instruction_pointer += 1
                        statement = self.intermediate_code[self.instruction_pointer]
                        statement = statement.split()
                        opcode = statement[0]


                    def_description.number_of_parameters = parameter_count
                    self.runtime.definition_description.append(def_description)

                    while opcode != "EXITDEFN":
                        self.instruction_pointer += 1
                        statement = self.intermediate_code[self.instruction_pointer]
                        statement = statement.split()
                        opcode = statement[0]
                        
                    self.instruction_pointer += 1
                    

                if opcode == "EXIT":
                    break
            else:
                self.instruction_pointer += 1



    def run(self):
        
        main_defined = False
        self.execution_pointer = 0
        current_definition = 0
        for def_desc_obj in self.runtime.definition_description:
            if(def_desc_obj.def_name == "main"):
                main_defined = True
                self.execution_pointer = def_desc_obj.def_start_location
                break
            current_definition += 1

        if main_defined == False:
            print("Program should contain a 'main' definition")
        else:
           
            self.execution_pointer += 1
          
            while self.execution_pointer < self.total_lines_of_code:
                statement = self.intermediate_code[self.execution_pointer]
                statement = statement.split()
                opcode = statement[0]
               
				# Save the intermediate computations in local stack of the definition 
                if opcode == "PUSH":
                    self.runtime.definition_description[current_definition].local_stack.append(statement[1])
                    self.execution_pointer += 1
				
				# Used for Assignment
                elif opcode == "SAVE":
                    variable =  statement[1]
                    if len(self.runtime.definition_description[current_definition].local_stack) > 0 :
                        value = self.runtime.definition_description[current_definition].local_stack.pop()
                    elif len(self.runtime.return_list) > 0:
                        value = self.runtime.return_list.pop()
                    if value in self.runtime.definition_description[current_definition].symbol_values:
                        value = self.runtime.definition_description[current_definition].symbol_values[value]

                    self.runtime.definition_description[current_definition].symbol_values[variable] = value
                    self.execution_pointer += 1

				# Return Definition. Switch context and populate return list of values.	
                elif opcode == "RETURN":
                    value = statement[1]
                    if (value in self.runtime.definition_description[current_definition].symbol_values):
                        value = self.runtime.definition_description[current_definition].symbol_values.get(value)
                    self.runtime.return_list.append(value)                
                    call_stack_obj = self.runtime.call_stack.pop()
                    self.execution_pointer = call_stack_obj['ep']
                    current_definition = call_stack_obj['def_id']

				# Pass arguments during Definition Invocation
                elif opcode == "PARAM":
                    value = statement[1]
                    if (value in self.runtime.definition_description[current_definition].symbol_values):
                        value = self.runtime.definition_description[current_definition].symbol_values.get(value)
                    self.runtime.param_list.append(value)
                    self.execution_pointer += 1

				# Goto the specified line in the program if the condition returns False
                elif opcode == "CONDFALSEGOTO":
                    check = self.runtime.definition_description[current_definition].local_stack.pop()
                   
                    if check == 'False' or check == False:
                        value = statement[1]
                      
                        self.execution_pointer = int(value)
                    else:
                        self.execution_pointer += 1
						
				# Goto the specified line in the program if the condition returns True
                elif opcode == "CONDTRUEGOTO":
                    check = self.runtime.definition_description[current_definition].local_stack.pop()
                   
                    if check == 'True' or check == True:
                        value = statement[1]

                        self.execution_pointer = int(value)
                    else:
                        self.execution_pointer += 1

				# Print value
                elif opcode == "PRINT":

                    if len(self.runtime.definition_description[current_definition].local_stack) > 0:
                        value = self.runtime.definition_description[current_definition].local_stack.pop()
                    elif len(self.runtime.return_list) > 0:
                        value = self.runtime.return_list.pop()

                    if value in self.runtime.definition_description[current_definition].symbol_values:
                        value = self.runtime.definition_description[current_definition].symbol_values[value]

                    print("Print: "+str(value))
                    self.execution_pointer += 1

				# Store current context and invoke definition
                elif opcode == "INVOKE":
                    definition_name = statement[1]
                    self.runtime.call_stack.append({'ep':self.execution_pointer + 1, 'def_id':current_definition})
                    current_definition = 0
                    for def_desc_obj in self.runtime.definition_description:
                        if (def_desc_obj.def_name == definition_name):
                            self.execution_pointer = def_desc_obj.def_start_location + 1
                            break
                        current_definition += 1

                    number_of_parameters = self.runtime.definition_description[current_definition].number_of_parameters
                    param_number = 0
                
                    while param_number < number_of_parameters:
                        value = self.runtime.param_list.pop()
                        
                        statement = self.intermediate_code[self.execution_pointer]
                        statement = statement.split()
                        opcode = statement[0]
                        if opcode != 'SAVE':
                            
                            sys.exit()
                        else:
                            variable = statement[1]
                            self.runtime.definition_description[current_definition].symbol_values[variable] = value
                            self.execution_pointer += 1
                            param_number += 1


                elif opcode == "ADD":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand1) + int(operand2)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "SUB":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand1) - int(operand2)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "MUL":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    
                    output = int(operand1) * int(operand2)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "DIV":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand2) / int(operand1)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "POW":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand1) ** int(operand2)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "MOD":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand2) % int(operand1)
                    self.runtime.definition_description[current_definition].local_stack.append(output)

                elif opcode == "GREATER":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand2) > int(operand1)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "LESSER":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand2) < int(operand1)
                    
                    
                    
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "GREATEREQUAL":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand2) >= int(operand1)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "LESSEREQUAL":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                   
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand2) <= int(operand1)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "EQUALS":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                   
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand2) == int(operand1)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "UNEQUALS":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                   
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand2) != int(operand1)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "AND":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                   
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand2) and int(operand1)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "OR":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand2) or int(operand1)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "EXITDEFN":
                    if self.runtime.definition_description[current_definition].def_name == 'main':
                        self.execution_pointer += 1
                      
                    else:              
                        call_stack_obj = self.runtime.call_stack.pop()
                        self.execution_pointer = call_stack_obj['ep']
                        current_definition = call_stack_obj['def_id']
                
                else:
                    self.execution_pointer += 1

# read the intermediate code from the file and store in a list					
test_file = open('intermediate.epsi')
testing = test_file.read()
intermediate_code = testing.split('\n')
intermediate_code.insert(0, "")
del intermediate_code[-1]

# initialize the interpreter
epsilonInterpreterObj = EpsilonInterpreter(intermediate_code)
# run the interpreter
epsilonInterpreterObj.run()