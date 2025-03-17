<template>
  <div class="login-body">
    <div class="bg"></div>
    <div class="login-panel">
      <el-form class="login-register" :model="formData" :rules="rules" ref="formDataRef" @submit.prevent>
        <div class="login-title">考勤系统</div>
        <!-- 账号 -->
        <el-form-item prop="userName">
          <el-input size="large" clearable placeholder="请输入员工账号" v-model.trim="formData.userName"
                    maxLength="150">
            <template #prefix>
              <span class="iconfont icon-account"></span>
            </template>
          </el-input>
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input type="password" size="large" placeholder="请输入密码" v-model.trim="formData.password" show-password
                    maxLength="150">
            <template #prefix>
              <span class="iconfont icon-password"></span>
            </template>
          </el-input>
        </el-form-item>

        <!-- 角色选择框 -->
        <el-form-item prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色">
            <el-option label="学生" value="STUDENT"></el-option>
            <el-option label="教师" value="TEACHER"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <div class="rememberme-panel">
            <el-checkbox>记住我</el-checkbox>
          </div>
          <div class="no-account">
            <a href="#" class="a-link" @click="toRegister">注册</a>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="op-btn" size="large" @click="doSubmit">
            <span>登录</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import {login} from "@/api/user.js"
import router from "@/router/index.js";
import {getCurrentInstance, ref, watch} from "vue";
import {ElMessage} from "element-plus";

const {proxy} = getCurrentInstance()

const formData = ref({
  role: 'STUDENT' // 默认选择学生角色
}); // 表单数据

// 表单验证规则
const rules = {
  userName: [{required: true, message: "请输入正确的账号"},],
  password: [{required: true, message: "请输入正确的密码"},],
};

// 获取表单引用
const formDataRef = ref(null);

/**
 * 跳转到联系管理员页面方法
 */
function toRegister() {
  // 这里可以根据实际情况修改跳转逻辑
  router.push('/register')
}

/**
 * 提交表单方法
 */
function doSubmit() {
  // 调用 validate 方法校验表单
  formDataRef.value.validate((valid) => {
    if (valid) {
      login(formData.value).then(res => {
        if (res.code === 200) {
          proxy.$common.setCookies(proxy.$config.tokenKeyName, res.data.token) //  存储登录状态
          proxy.$common.setCookies('name', res.data.userName)
          ElMessage.success("登录成功");
          // 根据角色跳转到不同页面
          if (formData.value.role === 'TEACHER') {
            router.push('/teacher'); // 教师端主页
          } else if (formData.value.role === 'STUDENT') {
            router.push('/student'); // 学生端主页
          }
        } else {
          ElMessage.error("账号或密码错误");
        }
      }).catch(err => {
        //请求失败，做相应处理
        console.log(err)
      })
    } else {
      // 如果校验不通过，提示用户补全信息
      ElMessage.error("请补全信息");
    }
  });
}
</script>

<style lang="scss" scoped>
.login-body {
  height: calc(100vh);
  background-size: cover;
  background-image: url("../assets/login背景.jpg");
  // background-color: rgb(16, 131, 232);
  display: flex;
}

.bg {
  flex: 1;
  background-size: cover;
  background-position: center;
  background-size: 800px;
  background-repeat: no-repeat;
  // background-image: url("../assets/cropped-1920-1080-1064066.png");
}

.login-panel {
  width: 430px;
  margin-right: 15%;
  margin-top: calc((100vh - 500px) / 2);

  .login-register {
    padding: 25px;
    background: #fff;
    border-radius: 5px;

    .login-title {
      text-align: center;
      font-size: 18px;
      font-weight: bold;
      margin-bottom: 20px;
    }

    .send-email-panel {
      display: flex;
      width: 100%;
      justify-content: space-between;

      .send-mail-btn {
        margin-left: 5px;
      }
    }

    .rememberme-panel {
      width: 100%;
    }

    .no-account {
      width: 100%;
      display: flex;
      justify-content: space-between;
    }

    .op-btn {
      width: 100%;
    }
  }
}

.check-code-panel {
  width: 100%;
  display: flex;

  .check-code {
    margin-left: 5px;
    cursor: pointer;
  }
}

.login-btn-qq {
  margin-top: 20px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;

  img {
    cursor: pointer;
    margin-left: 10px;
    width: 20px;
  }
}
</style>