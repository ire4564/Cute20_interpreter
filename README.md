# Make interpreter, about Cute20 language
프로그래밍언어개론을 배우며 만든 Cute20 language에 관한 interpreter를 구현함.<br/>
Cute20 language는 기존에 사용하는 scheme과 비슷하다.

![image](https://user-images.githubusercontent.com/44183221/86332457-8702c780-bc85-11ea-9f03-331774416cf8.png)

![image](https://user-images.githubusercontent.com/44183221/86332069-eb715700-bc84-11ea-9ae4-09b0f2e02191.png)

최종 구현물을 기준으로 하였을 때, 사용이 가능한 기능은 아래와 같다.

<h2>::function::</h2>

<b>Define:</b>  variable 또는 list, atom 등 정의 <br/>
<b>Car:</b>  list의 맨 처음 원소 리턴 <br/>
<b>Cdr:</b>  list의 맨 처음 원소를 제외한 나머지 list 리턴 <br/>
<b>Cons:</b>  한 개의 원소(head)와 한 개의 리스트(tail)을 붙여서 새로운 리스트를 만들어 리턴 <br/>
<b>Null?:</b>  리스트가 비었는지 안 비었는지 검사 후 리턴 <br/>
<b>EQ?:</b>  같은 객체인지 아닌지 검사 후 리턴 <br/>
<b>Atom?:</b>  객체가 atom인지 아닌지 검사 후 리턴, null은 atom으로 취급함 <br/>
<b>Not:</b>  T 혹은 F에 대한 결과값을 검사 후 리턴 <br/>
<b>Cond:</b>  조건문에 대한 처리 후 T에 해당하는 값을 리턴 <br/>
<b>Binary:</b>  일반적인 사칙연산 * / - + 에 대한 처리를 한 후 결과를 리턴 <br/>
<b>Lambda:</b>  무명 함수, 혹은 함수를 선언 후 결과 값에 맞도록 처리 <br/>
