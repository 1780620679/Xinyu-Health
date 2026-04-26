<template>
  <div class="skeleton" :class="{ 'skeleton-dark': dark }">
    <div v-if="type === 'card'" class="skeleton-card">
      <div class="skeleton-avatar"></div>
      <div class="skeleton-content">
        <div class="skeleton-title"></div>
        <div class="skeleton-text"></div>
        <div class="skeleton-text"></div>
        <div class="skeleton-text" style="width: 70%"></div>
      </div>
    </div>
    <div v-else-if="type === 'list'" class="skeleton-list">
      <div v-for="i in count" :key="i" class="skeleton-list-item">
        <div class="skeleton-avatar"></div>
        <div class="skeleton-content">
          <div class="skeleton-title"></div>
          <div class="skeleton-text"></div>
        </div>
      </div>
    </div>
    <div v-else-if="type === 'article'" class="skeleton-article">
      <div v-for="i in count" :key="i" class="skeleton-article-item">
        <div class="skeleton-image"></div>
        <div class="skeleton-info">
          <div class="skeleton-title"></div>
          <div class="skeleton-tag"></div>
          <div class="skeleton-text"></div>
          <div class="skeleton-text" style="width: 60%"></div>
        </div>
      </div>
    </div>
    <div v-else-if="type === 'circle'" class="skeleton-circle" :style="{ width: size, height: size }"></div>
    <div v-else-if="type === 'rect'" class="skeleton-rect" :style="{ width, height, borderRadius }"></div>
    <div v-else class="skeleton-rect" :style="{ width: '100%', height: '20px' }"></div>
  </div>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'rect',
    validator: (value) => ['card', 'list', 'article', 'circle', 'rect'].includes(value)
  },
  count: {
    type: Number,
    default: 3
  },
  width: {
    type: String,
    default: '100%'
  },
  height: {
    type: String,
    default: '20px'
  },
  size: {
    type: String,
    default: '40px'
  },
  borderRadius: {
    type: String,
    default: '4px'
  },
  dark: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped lang="scss">
.skeleton {
  .skeleton-card,
  .skeleton-list-item {
    display: flex;
    padding: 16px;
    border-radius: 8px;
    background: #f2f2f2;
    margin-bottom: 16px;

    .skeleton-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: linear-gradient(90deg, #f2f2f2 25%, #e6e6e6 50%, #f2f2f2 75%);
      background-size: 200% 100%;
      animation: loading 1.5s infinite;
      margin-right: 16px;
    }

    .skeleton-content {
      flex: 1;

      .skeleton-title {
        height: 20px;
        background: linear-gradient(90deg, #f2f2f2 25%, #e6e6e6 50%, #f2f2f2 75%);
        background-size: 200% 100%;
        animation: loading 1.5s infinite;
        margin-bottom: 12px;
        border-radius: 4px;
      }

      .skeleton-text {
        height: 16px;
        background: linear-gradient(90deg, #f2f2f2 25%, #e6e6e6 50%, #f2f2f2 75%);
        background-size: 200% 100%;
        animation: loading 1.5s infinite;
        margin-bottom: 8px;
        border-radius: 4px;
      }
    }
  }

  .skeleton-article {
    .skeleton-article-item {
      display: flex;
      background: #f2f2f2;
      border-radius: 12px;
      padding: 15px;
      margin-bottom: 20px;

      .skeleton-image {
        width: 240px;
        height: 150px;
        border-radius: 8px;
        background: linear-gradient(90deg, #f2f2f2 25%, #e6e6e6 50%, #f2f2f2 75%);
        background-size: 200% 100%;
        animation: loading 1.5s infinite;
        flex-shrink: 0;
      }

      .skeleton-info {
        flex: 1;
        margin-left: 20px;
        display: flex;
        flex-direction: column;
        gap: 12px;

        .skeleton-title {
          height: 24px;
          width: 70%;
          background: linear-gradient(90deg, #f2f2f2 25%, #e6e6e6 50%, #f2f2f2 75%);
          background-size: 200% 100%;
          animation: loading 1.5s infinite;
          border-radius: 4px;
        }

        .skeleton-tag {
          height: 24px;
          width: 80px;
          background: linear-gradient(90deg, #f2f2f2 25%, #e6e6e6 50%, #f2f2f2 75%);
          background-size: 200% 100%;
          animation: loading 1.5s infinite;
          border-radius: 12px;
        }

        .skeleton-text {
          height: 16px;
          background: linear-gradient(90deg, #f2f2f2 25%, #e6e6e6 50%, #f2f2f2 75%);
          background-size: 200% 100%;
          animation: loading 1.5s infinite;
          border-radius: 4px;
        }
      }
    }
  }

  .skeleton-circle,
  .skeleton-rect {
    background: linear-gradient(90deg, #f2f2f2 25%, #e6e6e6 50%, #f2f2f2 75%);
    background-size: 200% 100%;
    animation: loading 1.5s infinite;
  }

  &.skeleton-dark {
    .skeleton-card,
    .skeleton-list-item,
    .skeleton-article-item {
      background: #2d3748;

      .skeleton-avatar,
      .skeleton-image,
      .skeleton-title,
      .skeleton-text,
      .skeleton-tag {
        background: linear-gradient(90deg, #2d3748 25%, #4a5568 50%, #2d3748 75%);
      }
    }

    .skeleton-circle,
    .skeleton-rect {
      background: linear-gradient(90deg, #2d3748 25%, #4a5568 50%, #2d3748 75%);
    }
  }
}

@keyframes loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>