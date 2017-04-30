class DefinitionDescription:
    def __init__(self):
        self.def_name = ""
        self.def_start_location = 0
        self.number_of_parameters = 1
        self.symbol_values = {}
        self.local_stack = []


class RuntimeEnvironment:
    def __init__(self):
        self.definition_description = []
        self.call_stack = []


class EpsilonInterpreter:
    def __init__(self, intermediate_code=[]):
        self.runtime = RuntimeEnvironment()
        self.intermediate_code = intermediate_code
        self.instruction_pointer = 0
        self.total_lines_of_code = len(self.intermediate_code)
        print("total lines of code : " + str(self.total_lines_of_code))
        while self.instruction_pointer < self.total_lines_of_code:
            # print(statement)
            statement = self.intermediate_code[self.instruction_pointer]
            statement = statement.split()
            if len(statement) > 0:
                opcode = statement[0]
                # print(opcode)
                # self.instruction_pointer += 1
                if opcode == "DEFN":
                    def_description = DefinitionDescription()
                    print("Enter definition: " + statement[1])
                    def_description.def_name = statement[1]
                    def_description.def_start_location = self.instruction_pointer
                    self.instruction_pointer += 1
                    statement = self.intermediate_code[self.instruction_pointer]
                    statement = statement.split()
                    opcode = statement[0]

                    if opcode != "SAVE":
                        print("Definition " + def_description.def_name + " does not have any parameters")

                    parameter_count = 0
                    while opcode == "SAVE":
                        parameter_count += 1
                        def_parameter = statement[1]
                        def_description.symbol_values[def_parameter] = None
                        self.instruction_pointer += 1
                        statement = self.intermediate_code[self.instruction_pointer]
                        statement = statement.split()
                        opcode = statement[0]

                    print("parameter_count for " + def_description.def_name + " : " + str(parameter_count))
                    def_description.number_of_parameters = parameter_count
                    self.runtime.definition_description.append(def_description)
                    while opcode != "EXITDEFN":
                        self.instruction_pointer += 1
                        statement = self.intermediate_code[self.instruction_pointer]
                        statement = statement.split()
                        opcode = statement[0]
                        print("opcode: " + opcode)
                    self.instruction_pointer += 1
                    print("Line number after definition exit : " + str(self.instruction_pointer))

                if opcode == "EXIT":
                    break
            else:
                self.instruction_pointer += 1

        print("Total Definitions: " + str(len(self.runtime.definition_description)))
        for def_desc_obj in self.runtime.definition_description:
            print("Definition Name: " + def_desc_obj.def_name)
            print("Definition Start Location: " + str(def_desc_obj.def_start_location))
            print("Definition Number of Parameters: " + str(def_desc_obj.number_of_parameters))
            print("Definition Symbol Values: " + str(def_desc_obj.symbol_values))

    def run(self):
        print("Inside Run")
        main_defined = False
        self.execution_pointer = 0
        current_definition = 0
        for def_desc_obj in self.runtime.definition_description:
            if (def_desc_obj.def_name == "main"):
                main_defined = True
                self.execution_pointer = def_desc_obj.def_start_location
                break
            current_definition += 1

        if main_defined == False:
            print("Program should contain a 'main' definition")
        else:
            print("Program contains a 'main' definition")
            self.execution_pointer += 1
            print("Execution Pointer: " + str(self.execution_pointer))
            while self.execution_pointer < self.total_lines_of_code:
                statement = self.intermediate_code[self.execution_pointer]
                statement = statement.split()
                opcode = statement[0]
                print("opcode : " + opcode)
                if opcode == "PUSH":
                    self.runtime.definition_description[current_definition].local_stack.append(statement[1])
                    self.execution_pointer += 1

                elif opcode == "SAVE":
                    variable = statement[1]
                    value = self.runtime.definition_description[current_definition].local_stack.pop()
                    print("variable: " + variable + " , value: " + str(value))
                    self.runtime.definition_description[current_definition].symbol_values[variable] = value
                    self.execution_pointer += 1

                elif opcode == "ADD":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    print ("OP1----"+operand1)
                    print ("OP2----"+operand2)
                    print (self.runtime.definition_description[current_definition].symbol_values)
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    print ("OP1--c--" + operand1)
                    print ("OP2--c--" + operand2)
                    output = int(operand1) + int(operand2)
                    print ("OPp----"+str(output))
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                elif opcode == "SUB":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    print (self.runtime.definition_description[current_definition].symbol_values)
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
                    print (self.runtime.definition_description[current_definition].symbol_values)
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
                    print (self.runtime.definition_description[current_definition].symbol_values)
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
                    print (self.runtime.definition_description[current_definition].symbol_values)
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
                    print (self.runtime.definition_description[current_definition].symbol_values)
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand2) % int(operand1)
                    self.runtime.definition_description[current_definition].local_stack.append(output)

                elif opcode == "GREATER":
                    operand1 = self.runtime.definition_description[current_definition].local_stack.pop()
                    operand2 = self.runtime.definition_description[current_definition].local_stack.pop()
                    print (self.runtime.definition_description[current_definition].symbol_values)
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
                    print (self.runtime.definition_description[current_definition].symbol_values)
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
                    print (self.runtime.definition_description[current_definition].symbol_values)
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
                    print (self.runtime.definition_description[current_definition].symbol_values)
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
                    print (self.runtime.definition_description[current_definition].symbol_values)
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
                    print (self.runtime.definition_description[current_definition].symbol_values)
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
                    print (self.runtime.definition_description[current_definition].symbol_values)
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
                    print (self.runtime.definition_description[current_definition].symbol_values)
                    if(operand1 in self.runtime.definition_description[current_definition].symbol_values):
                        operand1 = self.runtime.definition_description[current_definition].symbol_values.get(operand1)
                    if (operand2 in self.runtime.definition_description[current_definition].symbol_values):
                        operand2 = self.runtime.definition_description[current_definition].symbol_values.get(operand2)
                    output = int(operand2) or int(operand1)
                    self.runtime.definition_description[current_definition].local_stack.append(output)
                    self.execution_pointer += 1

                else:
                    self.execution_pointer += 1

            print("Total Definitions: " + str(len(self.runtime.definition_description)))
            for def_desc_obj in self.runtime.definition_description:
                print("Definition Name: " + def_desc_obj.def_name)
                print("Definition Start Location: " + str(def_desc_obj.def_start_location))
                print("Definition Number of Parameters: " + str(def_desc_obj.number_of_parameters))
                print("Definition Symbol Values: " + str(def_desc_obj.symbol_values))


intermediate_code = ["",
                     "DEFN fibonacci",
                     "SAVE n",
                     "PUSH 0",
                     "SAVE a",
                     "PUSH 1",
                     "SAVE b",
                     "PUSH 0",
                     "SAVE i",
                     "PUSH 0",
                     "SAVE c",
                     "PUSH n",
                     "PUSH 1",
                     "EQUALS",
                     "CONDFALSEGOTO 19",
                     "PUSH 0",
                     "PRINT",
                     "PUSH True",
                     "CONDTRUEGOTO 49",
                     "PUSH n",
                     "PUSH 2",
                     "EQUALS",
                     "CONDFALSEGOTO 27",
                     "PUSH 1",
                     "PRINT",
                     "PUSH True",
                     "CONDTRUEGOTO 49",
                     "PUSH i",
                     "PUSH 2",
                     "PUSH n",
                     "SUB",
                     "LESSER",
                     "CONDFALSEGOTO 47",
                     "PUSH b",
                     "PUSH a",
                     "ADD",
                     "SAVE c",
                     "PUSH b",
                     "SAVE a",
                     "PUSH c",
                     "SAVE b",
                     "PUSH 1",
                     "PUSH i",
                     "ADD",
                     "SAVE i",
                     "PUSH True",
                     "CONDTRUEGOTO 27",
                     "PUSH c",
                     "PRINT",
                     "EXITDEFN fibonacci",
                     "DEFN main",
                     "PUSH 1",
                     "SAVE a",
                     "PUSH 0",
                     "SAVE b",
                     "PUSH b",
                     "PUSH a",
                     "AND",
                     "SAVE c",
                     "PUSH 5",
                     "SAVE n",
                     "INVOKE fibonacci",
                     "EXITDEFN main",
                     "EXIT"]

epsilonInterpreterObj = EpsilonInterpreter(intermediate_code)
epsilonInterpreterObj.run()