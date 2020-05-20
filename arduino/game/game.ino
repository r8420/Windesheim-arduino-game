const int buttonPin = 2;     // the number of the pushbutton pin
const int buttonPin2 =  3;
int potPin = A0; 

// variables will change:
int buttonState = 0;         // variable for reading the pushbutton status
int buttonState2 = 0;
int potState = 0;
int potCheck = 0;


boolean buttonCheck = true;
boolean buttonCheck2 = true;
void setup() {
  Serial.begin(9600);
  pinMode(buttonPin2, INPUT);
  pinMode(buttonPin, INPUT);
}

void loop() {  
  buttonState = digitalRead(buttonPin);
  buttonState2 = digitalRead(buttonPin2);
  potState = analogRead(potPin);
  
  if (buttonState == HIGH) {
    if(buttonCheck){
      buttonCheck = false;
      Serial.print("A");
    }
  } else {
    if(!buttonCheck){
      Serial.print("a");
    }
    buttonCheck = true;
    
  }
  if (buttonState2 == HIGH) {
    if(buttonCheck2){
      buttonCheck2 = false;
      Serial.print("B");
    }
    
  } else {
    if(!buttonCheck2){
      Serial.print("b");
    }
    buttonCheck2 = true;
  }
  if(potState > potCheck+10 || potState < potCheck-10){
    int temp = round(potState/110);
    Serial.print(temp);
    potCheck = potState;
  }
}
