### Oauth2提供的默认端点（endpoints）
 + /oauth/authorize：授权端点
 + /oauth/token：令牌端点
 + /oauth/confirm_access：用户确认授权提交端点
 + /oauth/error：授权服务错误信息端点
 + /oauth/check_token：用于资源服务访问的令牌解析端点
 + /oauth/token_key：提供公有密匙的端点，如果使用JWT令牌的话

#### 一、授权码模式

1. 说明

   ```
   可用开放平台账户给第三方商户，商户申请client_id和密码请求用户授权，用户授权商户即可调用平台授权的接口获取数据，类似微信、支付宝授权登陆
   ```

2. 获取 code ---->浏览器访问

   ```http
   http://localhost:8080/oauth/authorize?response_type=code&client_id=code&redirect_uri=http://www.baidu.com&scope=read&state=test
   ```
   + ``client_id:``  商户申请的 client_id ( oauth_client_details 表中的记录)

   + ``state:`` 该参数在跳转回去时原样带回

   + ``redirect_uri``： 该参数要与商户申请 client_id 时登记的 url (oauth_client_details 表中 的web_server_redirect_uri字段) 一样

   + ``response_type:`` 固定值：code

   + ``scope:`` oauth_client_details 标志的 scope 字段

3. 用户点击 ``授权`` 或者 ``拒绝``

4. 浏览器得到结果

   ```http
   http://www.baidu.com/?code=kqdjFs&state=test
   ```

5. 用 code 换取 token

   ```http
   curl -X POST --user code:123456 http://localhost:8080/oauth/token -H "content-type: application/x-www-form-urlencoded" -d "code=kqdjFs&grant_type=authorization_code&redirect_uri=http://www.baidu.com&scope=read&state=test"
   ```
   
   +  ``--user code:123456`` : 填写 oauth_client_details 表中 申请的 client 的 id 和 密码
   + ``code=kqdjFs``: 填写 上一步 获取的 code 值
   + ``grant_type``:  固定值 authorization_code
   + ``redirect_uri``：该参数要与商户申请 client_id 时登记的 url (oauth_client_details 表中 的web_server_redirect_uri字段) 一样
   + ``state:`` 该参数在跳转回去时原样带回
   + ``scope:`` oauth_client_details 标志的 scope 字段
   
   或者 使用 postman 的方式
   
   ![](./授权码模式 code换取 token.png)

#### 二、密码模式

1. 说明

   ```java
   可用于用户通过前端应用登陆、使用应用，如app，web等终端
   ```

2. 不需要 回调地址，直接获取 token

   ```http
   curl -X POST --user password:123456 http://localhost:8080/oauth/token -H "accept: application/json" -H "content-type: application/x-www-form-urlencoded" -d "grant_type=password&username=admin&password=123456&scope=read"
   ```
    + ``--user password:123456`` : 填写 oauth_client_details 表中 申请的 client 的 id 和 密码
    + ``grant_type``:  固定值 password
    + ``username:``  登录的用户名
    + ``password:`` 登录的密码
    + ``state:`` 该参数在跳转回去时原样带回
    + ``scope:`` oauth_client_details 标志的 scope 字段


#### 三、客户端模式

1. 说明

   ```
   可用于接口开放给第三方商户，商户申请client_id和密码，即可调用授权的接口
   ```

2. 不需要 回调地址，直接获取 token ---> 没有 RefreshToken

   ```java
   curl -X POST "http://localhost:8080/oauth/token" --user client:123456 -d "grant_type=client_credentials&scope=read&&state=test"
   ```
      + ``client_id:``  商户申请的 client_id ( oauth_client_details 表中的记录)
   
      + ``state:`` 该参数在跳转回去时原样带回
   
      + ``response_type:`` 固定值：client_credentials
   
      + ``scope:`` oauth_client_details 标志的 scope 字段
   
   


#### 四、简化模式

1. 说明

   ```
   就是 授权码模式的 简化版，返回的不是 code，而是直接可以访问的 令牌
   ```

2. 调用方法

   ```http
   http://localhost:8080/oauth/authorize?client_id=implicit&redirect_uri=http://www.baidu.com&response_type=implicit&scope=read&state=test
   ```
   + ``client_id:``  商户申请的 client_id ( oauth_client_details 表中的记录)

   + ``state:`` 该参数在跳转回去时原样带回

   + ``redirect_uri``： 该参数要与商户申请 client_id 时登记的 url (oauth_client_details 表中 的web_server_redirect_uri字段) 一样

   + ``response_type:`` 固定值：implicit

   + ``scope:`` oauth_client_details 标志的 scope 字段

#### 五、刷新 token

1. 说明

   ```java
   使用refresh_token更新access_token
   ```

2. 调用方法

   ```http
   curl -i -X POST -u 'password:123456'  http://localhost:8080/oauth/token -H "accept: application/json" -d 'grant_type=refresh_token&refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxLCJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbInJlYWQiXSwiYXRpIjoiNjFhOTBmNmItMDU4NC00OTU2LWI4ZmYtZjc5MWY1Zjk1YTMzIiwiZXhwIjoxNTkxOTM3MzI1LCJhdXRob3JpdGllcyI6WyJST0xFX1RFU1QiLCJST0xFX0FETUlOIl0sImp0aSI6IjhkY2Q4MWNjLTZiZDktNDMzMy1hYWJhLTFkODI5OThlNjZmNSIsImNsaWVudF9pZCI6InBhc3N3b3JkIiwidXNlcm5hbWUiOiJhZG1pbiJ9.iGHUDIesPBr2fm9wRCeLFsQjanR8AjhqXaMxBAr1rB8'
   ```
   + ``client_id:``  商户申请的 client_id ( oauth_client_details 表中的记录)

   + ``response_type:`` 固定值：refresh_token

   + ``refresh_token:`` 签发 token中所带的 token

#### 六、效验jwt Token

1. 说明

   ```
   效验 token的值
   ```

   

2. 调用方法

   ```http
   curl -X GET --user password:123456 http://localhost:8080/oauth/check_token -H "accept: application/json" -H "content-type: application/x-www-form-urlencoded" -d "token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxLCJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbInJlYWQiXSwiZXhwIjoxNjAyMTI4Mjg2LCJhdXRob3JpdGllcyI6WyJST0xFX1RFU1QiLCJST0xFX0FETUlOIl0sImp0aSI6IjUwZmYzNzNjLTc2MWYtNDcyOS04ZDJhLWY1N2I2NmY2Nzc2ZSIsImNsaWVudF9pZCI6InBhc3N3b3JkIiwidXNlcm5hbWUiOiJhZG1pbiJ9.Ev3628-OzwQnpfsk2qg0-iU6KNoV76aHT-4jTkin_hM" 
   ```

