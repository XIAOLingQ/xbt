import { onMounted, onUnmounted } from 'vue'

export function useIntersectionObserver(options = { threshold: 0.1 }) {
  let observer = null

  const observe = (targets) => {
    if (!observer || !targets) return
    targets.forEach(target => {
      if (target) {
        observer.observe(target)
      }
    })
  }

  onMounted(() => {
    observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('visible')
          observer.unobserve(entry.target) // Stop observing after it becomes visible
        }
      })
    }, options)
  })

  onUnmounted(() => {
    if (observer) {
      observer.disconnect()
    }
  })

  return { observe }
}

