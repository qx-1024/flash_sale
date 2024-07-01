import { ref } from 'vue'
import { defineStore } from 'pinia';

export const useProductStore = defineStore(
  'product',
  () => {
    const currentProductId = ref(null);

    function setProductId(productId) {
      currentProductId.value = productId;
    }

    return { currentProductId, setProductId };
  },
  {
    persist: true,
  }
);