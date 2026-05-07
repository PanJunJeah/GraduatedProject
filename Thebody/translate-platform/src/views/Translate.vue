<template>
  <div class="translate-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h2>Transformer 交互式翻译平台</h2>
          <div class="header-actions">
            <el-button type="text" @click="$router.push('/translate/batch')">
              <el-icon><DocumentCopy /></el-icon> 批量翻译
            </el-button>
            <el-button type="text" @click="$router.push('/history')">
              <el-icon><Clock /></el-icon> 翻译历史
            </el-button>
            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                <el-icon><User /></el-icon> {{ user?.username }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <el-main>
        <div class="translate-container">
          <el-card>
            <div class="language-selector">
              <div class="language-group">
                <span class="label">源语言</span>
                <el-select v-model="sourceLang" @change="handleLangChange">
                  <el-option label="中文" value="zh" />
                  <el-option label="英文" value="en" />
                  <el-option label="日文" value="ja" />
                  <el-option label="韩文" value="ko" />
                  <el-option label="法文" value="fr" />
                  <el-option label="德文" value="de" />
                  <el-option label="西班牙文" value="es" />
                </el-select>
              </div>

              <el-button circle @click="handleSwap" :disabled="isTranslating">
                <el-icon><Swap /></el-icon>
              </el-button>

              <div class="language-group">
                <span class="label">目标语言</span>
                <el-select v-model="targetLang" @change="handleLangChange">
                  <el-option label="中文" value="zh" />
                  <el-option label="英文" value="en" />
                  <el-option label="日文" value="ja" />
                  <el-option label="韩文" value="ko" />
                  <el-option label="法文" value="fr" />
                  <el-option label="德文" value="de" />
                  <el-option label="西班牙文" value="es" />
                </el-select>
              </div>
            </div>

            <div class="translation-area">
              <div class="text-panel">
                <el-input
                  v-model="sourceText"
                  type="textarea"
                  :rows="8"
                  placeholder="请输入要翻译的文本..."
                  resize="none"
                />
                <div class="panel-footer">
                  <span class="char-count">{{ sourceText.length }} 字符</span>
                  <el-button @click="handleClear" size="small">清空</el-button>
                </div>
              </div>

              <div class="divider">
                <el-icon class="arrow-icon"><Right /></el-icon>
              </div>

              <div class="text-panel">
                <el-input
                  v-model="translatedText"
                  type="textarea"
                  :rows="8"
                  placeholder="翻译结果将在此显示..."
                  resize="none"
                  readonly
                />
                <div class="panel-footer">
                  <span class="char-count">{{ translatedText.length }} 字符</span>
                  <el-button @click="handleCopy" size="small" :disabled="!translatedText">
                    <el-icon><DocumentCopy /></el-icon> 复制
                  </el-button>
                </div>
              </div>
            </div>

            <div class="action-area">
              <el-button type="primary" size="large" @click="handleTranslate" :loading="isTranslating">
                <el-icon v-if="!isTranslating"><Translate /></el-icon>
                {{ isTranslating ? '翻译中...' : '翻译' }}
              </el-button>
            </div>
          </el-card>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '../stores/auth';
import { useTranslationStore } from '../stores/translation';

const router = useRouter();
const authStore = useAuthStore();
const translationStore = useTranslationStore();

const user = computed(() => authStore.user);
const sourceText = ref('');
const translatedText = ref('');
const sourceLang = ref('zh');
const targetLang = ref('en');
const isTranslating = computed(() => translationStore.isTranslating);

translationStore.$subscribe((mutation, state) => {
  translatedText.value = state.translatedText;
});

const handleLangChange = () => {
  translationStore.setSourceLang(sourceLang.value);
  translationStore.setTargetLang(targetLang.value);
};

const handleSwap = () => {
  const tempText = sourceText.value;
  sourceText.value = translatedText.value;
  translatedText.value = tempText;

  const tempLang = sourceLang.value;
  sourceLang.value = targetLang.value;
  targetLang.value = tempLang;

  handleLangChange();
};

const handleTranslate = async () => {
  if (!sourceText.value.trim()) {
    ElMessage.warning('请输入要翻译的文本');
    return;
  }

  translationStore.setSourceText(sourceText.value);
  const result = await translationStore.translate();

  if (!result.success) {
    ElMessage.error(result.message || '翻译失败');
  }
};

const handleClear = () => {
  sourceText.value = '';
  translatedText.value = '';
  translationStore.clear();
};

const handleCopy = async () => {
  try {
    await navigator.clipboard.writeText(translatedText.value);
    ElMessage.success('已复制到剪贴板');
  } catch {
    ElMessage.error('复制失败');
  }
};

const handleCommand = (command) => {
  if (command === 'logout') {
    authStore.logout();
    router.push('/login');
  }
};
</script>

<style scoped>
.translate-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.el-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.header-content h2 {
  margin: 0;
  color: #333;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  color: #409eff;
}

.translate-container {
  max-width: 1200px;
  margin: 20px auto;
}

.language-selector {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.language-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.language-group .label {
  font-weight: bold;
  color: #666;
}

.translation-area {
  display: flex;
  gap: 20px;
  align-items: stretch;
}

.text-panel {
  flex: 1;
}

.panel-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.char-count {
  color: #999;
  font-size: 12px;
}

.divider {
  display: flex;
  align-items: center;
  padding: 0 10px;
}

.arrow-icon {
  font-size: 24px;
  color: #409eff;
}

.action-area {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
