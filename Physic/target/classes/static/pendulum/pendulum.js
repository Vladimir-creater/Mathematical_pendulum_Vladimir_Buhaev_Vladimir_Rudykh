class PendulumAnimator {
    constructor(canvasId, params) {
        this.canvas = document.getElementById(canvasId);
        if (!this.canvas) return;
        this.ctx = this.canvas.getContext('2d');
        this.params = params || {};
        this.g = this.params.gValue || 9.81;
        this.L = this.params.length || 1.0;
        this.theta = this.params.angle ? this.params.angle * Math.PI / 180 : Math.PI / 6;
        this.omega = 0;
        this.beta = 0.02;
        this.dt = 0.016;
        this.scale = Math.min(120, this.canvas.width / 3.5 / this.L);
        this.pivotX = this.canvas.width / 2;
        this.pivotY = 35;
        this.bobRadius = 14;
        this.running = true;
        this.animate();
    }
    step() {
        const alpha = -(this.g / this.L) * Math.sin(this.theta) - this.beta * this.omega;
        this.omega += alpha * this.dt;
        this.theta += this.omega * this.dt;
    }
    draw() {
        const ctx = this.ctx;
        const { width, height } = this.canvas;
        ctx.clearRect(0, 0, width, height);
        const bobX = this.pivotX + this.L * this.scale * Math.sin(this.theta);
        const bobY = this.pivotY + this.L * this.scale * Math.cos(this.theta);
        ctx.strokeStyle = '#4b5563';
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.moveTo(this.pivotX, this.pivotY);
        ctx.lineTo(bobX, bobY);
        ctx.stroke();
        const gradient = ctx.createRadialGradient(bobX - 4, bobY - 4, 2, bobX, bobY, this.bobRadius);
        gradient.addColorStop(0, '#93c5fd');
        gradient.addColorStop(1, '#3b82f6');
        ctx.fillStyle = gradient;
        ctx.beginPath();
        ctx.arc(bobX, bobY, this.bobRadius, 0, Math.PI * 2);
        ctx.fill();
        ctx.strokeStyle = '#1e40af';
        ctx.lineWidth = 1.5;
        ctx.stroke();
        ctx.fillStyle = '#dc2626';
        ctx.beginPath();
        ctx.arc(this.pivotX, this.pivotY, 6, 0, Math.PI * 2);
        ctx.fill();
        ctx.fillStyle = '#6b7280';
        ctx.font = '12px monospace';
        ctx.fillText(`θ = ${(this.theta * 180 / Math.PI).toFixed(1)}°`, 12, 22);
    }
    animate() {
        if (!this.running) return;
        this.step();
        this.draw();
        requestAnimationFrame(() => this.animate());
    }
    reset(params) {
        this.params = { ...this.params, ...params };
        this.g = this.params.gValue || 9.81;
        this.L = this.params.length || 1.0;
        this.theta = this.params.angle ? this.params.angle * Math.PI / 180 : Math.PI / 6;
        this.omega = 0;
        this.scale = Math.min(120, this.canvas.width / 3.5 / this.L);
    }
    stop() { this.running = false; }
    start() { if (!this.running) { this.running = true; this.animate(); } }
}

document.addEventListener('DOMContentLoaded', function() {
    const canvas = document.getElementById('pendulum-canvas');
    if (canvas) {
        const params = {
            gValue: parseFloat(canvas.dataset.g) || 9.81,
            length: parseFloat(canvas.dataset.length) || 1.0,
            angle: parseFloat(canvas.dataset.angle) || 30
        };
        window.pendulumAnim = new PendulumAnimator('pendulum-canvas', params);
    }
});